

'use strict';


/**
 *
 * @param {ru.nsk.decentury.ResponseToRequest} response - model instance
 * @transaction
 */
async function onResponseToRequest(response) {
    console.log('Response to request');

    if (response.request.state !== 'SENT') {
        throw new Error('Response is already processed');
    }

    // создаем объект Deal

    const factory = getFactory();
    const NS = 'ru.nsk.decentury';

    const deal = factory.newResource(NS, 'Deal', 'Deal_' + response.request.id)
    deal.seller = response.request.seller
    deal.recipient = response.request.recipient

    deal.products = []

    for (let i = 0; i < response.products.length; i++) {
        if (response.products[i].owner !== response.request.seller) {
            throw new Error("Seller can not sale another's products");
        }

        deal.products.push(response.products[i]);
    }

    deal.amount = response.request.amount
    deal.vendorCode = response.request.vendorCode
    deal.price = response.request.price

    deal.request = response.request
    deal.state = 'SENT'

    const dealRegistry = await getAssetRegistry(NS + '.Deal');
    await dealRegistry.add(deal);


    // меняем состояние запроса

    response.request.state = 'EXECUTED';

    const r = await getAssetRegistry('ru.nsk.decentury.Request');
    await r.update(response.request);
}

function getUserType(user) {
    let user_type = '' + user;
    let pos = user_type.search('#');
    return user_type.substr(13, pos - 13)
}

/**
 *
 * @param {ru.nsk.decentury.ProductTransition} transition - model instance
 * @transaction
 */
async function onProductTransition(transition) {
    console.log('Product transition');

    if (transition.deal.state == 'RECIEVED') {
        throw new Error('Deal is already in state RECIEVED');
    }

    // проверяем, что все товары принадлежат продавцу
    for (let i = 0; i < transition.deal.products.length; i++) {
        if (transition.deal.products[i].owner != transition.deal.seller) {
            throw new Error("Seller can not sale another's products");
        }
    }

    // проверяем, что у покупателя достаточно средств на счете
    if (transition.deal.recipient.balance < transition.deal.price) {
        throw new Error('Recipient has not enought money');
    }

    // меняем состояние сделки
    transition.deal.state = 'RECIEVED';

    const d = await getAssetRegistry('ru.nsk.decentury.Deal');
    await d.update(transition.deal);


    // меняем влядельцев товаров
    const product = await getAssetRegistry('ru.nsk.decentury.Product');

    for (let i = 0; i < transition.deal.products.length; i++) {
        transition.deal.products[i].owner = transition.deal.recipient;
        
        await product.update(transition.deal.products[i]);
    }


    // переводим деньги на счет продавца
    transition.deal.seller.balance += transition.deal.price;

    const user = await getParticipantRegistry(getUserType(transition.deal.seller));
    await user.update(transition.deal.seller);

    transition.deal.recipient.balance -= transition.deal.price;
    const user2 = await getParticipantRegistry(getUserType(transition.deal.recipient));
    await user2.update(transition.deal.recipient);
}



/* Сумма за все товары, которые хочет купить покупатель */
function getPrice(products, prices) {
    let sum = 0;
    for(let i = 0; i < products.length; i++) {
        for (let j = 0; j < prices.length; j++) {
            if (products[i].vendorCode == prices[j].vendorCode)
                sum += prices[j].price;
        }
    }
    return sum;
}

function getPriceOf(priceList, product) {
    for (let i = 0; i < priceList.length; i++) {
        if (priceList[i].vendorCode === product.vendorCode) {
            return priceList[i].price;
        }
    }
}

function getBonusBalance(buyer, store) {
    let bonusBalance = 0;
    for (let i = 0; i < buyer.wallet.length; i++) {
        if (buyer.wallet[i].store == store) {
            bonusBalance = buyer.wallet[i].tokenBalance;
            break;
        }
    }
    return bonusBalance;
}

function changeBonusBalance(buyer, store, amount, NS) {
    for (let i = 0; i < buyer.wallet.length; i++) {
        if (buyer.wallet[i].store == store) {
            buyer.wallet[i].tokenBalance += amount;
            return;
        }
    }
    // Если у покупателя еще нет бонусов от данного продавца, заводим новую запись
    if (amount <= 0) {
        return;
    }
    const factory = getFactory();
    const tokenBalance = factory.newConcept(NS, 'TokenBalance');
    tokenBalance.store = store;
    tokenBalance.tokenBalance = amount;

    buyer.wallet.push(tokenBalance);
}


/**
 *
 * @param {ru.nsk.decentury.buyingGoodsInStore} buying - model instance
 * @transaction
 */
async function onbuyingGoodsInStore(buying) {
    const NS = 'ru.nsk.decentury';
    let sum;
    console.log('Buying goods in a store');

    let buyerBonusBalance = getBonusBalance(buying.buyer, buying.store);

    if (buying.bonus < 0) {
        throw new Error("Bonus can not be negative.");
    }
    if (buying.bonus > buyerBonusBalance) {
        throw new Error("Buyer has not as many bonuses.");
    }

    // проверяем, что все товары принадлежат продавцу
    for (let i = 0; i < buying.products.length; i++) {
        if (buying.products[i].owner !== buying.store) {
            throw new Error("Seller can not sale another's products");
        }
    }
    //проверяем, что у покупателя достаточно средств
    sum = getPrice(buying.products, buying.store.priceList);
    if (buying.bonus > sum * buying.store.maxBonuses) {
        buying.bonus = sum * buying.store.maxBonuses;
    }
    if ((buying.buyer.balance + buying.bonus) < sum) {
        throw new Error("Buyer has not enought money");
    }

    // меняем состояние товара и его владельца
    const product = await getAssetRegistry('ru.nsk.decentury.Product');
    for (let i = 0; i < buying.products.length; i++) {

        // создаем объект Guarantee
        const factory = getFactory();        
        const guarantee = factory.newResource(NS, 'Guarantee', 'Guarantee_' + buying.products[i].id);
        guarantee.product = buying.products[i];
        guarantee.seller = buying.products[i].owner;
        guarantee.price = getPriceOf(buying.store.priceList, buying.products[i]);
        const guaranteeRegistry = await getAssetRegistry(NS + '.Guarantee');
        await guaranteeRegistry.add(guarantee);
        
        buying.products[i].owner = buying.buyer;
        buying.products[i].state = "BOUGHT";
        await product.update(buying.products[i]);
    }
       
    // переводим деньги на счет продавца
    buying.store.balance = buying.store.balance + sum - buying.bonus;

    const user = await getParticipantRegistry(getUserType(buying.store));
    await user.update(buying.store);
    
    //списываем деьги с покупателя
    const user2 = await getParticipantRegistry(getUserType(buying.buyer));
    buying.buyer.balance = buying.buyer.balance - (sum - buying.bonus);

    // buying.buyer.bonusBalance = buying.buyer.bonusBalance - buying.bonus + sum * buying.store.bonusCoefficient;
    changeBonusBalance(buying.buyer, buying.store, sum * buying.store.bonusCoefficient - buying.bonus, NS);

    await user2.update(buying.buyer);
}

async function onprovideGuarantee(pg) {
    if (pg.guarantee.product.owner !== pg.buyer) {
        throw new Error("Buyer is not an owner");
    }
    if (pg.guarantee.product.producer.balance < pg.guarantee.price) {
        pg.buyer.balance = pg.buyer.balance + pg.guarantee.product.producer.balance;
        pg.guarantee.product.producer.balance = 0;
    }   else {
        pg.buyer.balance = pg.buyer.balance + pg.guarantee.price;
        pg.guarantee.product.producer.balance = pg.guarantee.product.producer.balance - pg.guarantee.price;
    }
    pg.guarantee.product.owner = pg.guarantee.product.producer;
    

    const producerRegister = await getParticipantRegistry(getUserType(pg.guarantee.product.producer));
    await producerRegister.update(pg.guarantee.product.producer);
    
    const buyerRegister = await getParticipantRegistry(getUserType(pg.buyer));
    await buyerRegister.update(pg.buyer);

    const productRegister = await getAssetRegistry('ru.nsk.decentury.Product');
    await productRegister.update(pg.guarantee.product);
}


/**
 *
 * @param {ru.nsk.decentury.BonusesExchange} exchange - model instance
 * @transaction
 */
async function onBonusesExchange(exchange) {
    const NS = 'ru.nsk.decentury';
    console.log('Bonuses exchange');

    let buyerBonusBalance = getBonusBalance(exchange.buyer, exchange.fromStore);

    if (exchange.bonusesAmount > buyerBonusBalance) {
        throw new Error("Buyer has not as many bonuses.");
    }

    //меняем бонусы
    changeBonusBalance(exchange.buyer, exchange.fromStore, -exchange.bonusesAmount, NS);
    changeBonusBalance(exchange.buyer, exchange.toStore, exchange.bonusesAmount, NS);

    const user = await getParticipantRegistry(getUserType(exchange.buyer));
    await user.update(exchange.buyer);
}
