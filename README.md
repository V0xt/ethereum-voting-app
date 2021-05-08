# Ethereum Voting App
[![Maintainability](https://api.codeclimate.com/v1/badges/aab6fef4f402f9bb29f6/maintainability)](https://codeclimate.com/github/v0xat/ethereum-voting-app/maintainability)
[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)

Простая система голосования с использованием Ethereum и Java.

## Запуск и сборка:
### Необходимые библиотеки:
1. [Web3j](https://github.com/web3j/web3j/releases)
2. [web3j-cli](https://github.com/web3j/web3j-cli)
3. [Ganache](https://www.trufflesuite.com/ganache)
4. [solc](https://github.com/ethereum/solidity/releases)

### Как запустить:
1. Скачиваем репозиторий и импортируем проект в IntelliJ IDEA. Maven автоматически загрузит все зависимости
2. Скачиваем и устанавливаем [Ganache](https://www.trufflesuite.com/ganache)
3. Запускаем сервер [Ganache](https://www.trufflesuite.com/ganache) по адресу HTTP://127.0.0.1:7545
4. Запускаем приложение
5. Вводим приватный ключ

#### Примечания
Если код контракта был изменён, то Java шаблон должен быть [сгенерирован](https://kauri.io/generate-a-java-wrapper-from-your-smart-contract/84475132317d4d6a84a2c42eb9348e4b/a) заново.


