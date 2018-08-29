# Децентрализованная платформа управления программами лояльности Бонбон
Решение кейса №9 (Финнополис 2018) от команды Decentury.

Проект включает в себя три части:
  - Блокчейн модуль (директория hyperledger).
  - Мобильное приложение под Android (директория android).
  - Web-интерфейс (директория web).
  
В качестве блокчейн-платформы использовался Hyperledger Fabric v.1.1. Блокчейн-модуль реализован средствами Hyperledger Composer. Для тестирования и демонстрации нами поднят локальный инстанс Hyperledger Fabric, где проводятся все транзакции. Доступ к Fabric осуществляется через Hyperledger Composer REST-сервер, адрес 37.193.252.60:3000.
