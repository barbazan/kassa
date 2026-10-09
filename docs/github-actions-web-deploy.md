# Выкладка kassa через GitHub Actions

Workflow: `.github/workflows/deploy-web.yml`. После push в **master** GitHub сначала проверяет `GameConfig.TARGET_PLATFORM`. Только при `TargetPlatform.HTML_VK` запускается выкладка: GitHub запускает браузерные и Java-тесты, проверяет сценарии выкладки и отката, собирает `html/build/dist` и публикует эту сборку на <https://mir-game.ru/kassa/>. Готовую папку `html/build/dist` коммитить не нужно.

Есть ручной запуск: Actions → Deploy web version → Run workflow → master. Другие ветки и другие значения `TARGET_PLATFORM` не публикуются; при другой платформе пропускается весь job сборки и выкладки. После ошибки можно использовать Re-run jobs: новая попытка получает отдельную резервную копию.

## Однократная настройка

Из PowerShell в корне kassa:

```powershell
# Подготовить отдельный ключ и скрипт настройки без подключения к серверу.
.\scripts\setup-web-deploy.ps1

# Установить ограниченный доступ на сервер и проверить подключение.
.\scripts\setup-web-deploy.ps1 -Install
```

Для установки используется существующий личный ключ `%USERPROFILE%\.ssh\id_rsa`. Другой ключ можно передать через `-IdentityFile`. Отпечаток сервера берётся из уже проверенного локального `known_hosts`; непроверенный результат `ssh-keyscan` не принимается.

Подготовленные файлы находятся в игнорируемой папке `build/github-web-deploy/credentials`. Скрипт не выводит содержимое приватного ключа.

Откройте [секреты Actions репозитория kassa](https://github.com/barbazan/kassa/settings/secrets/actions) и добавьте два **Repository secrets**:

| Имя | Значение |
| --- | --- |
| `DEPLOY_SSH_KEY` | Содержимое `build/github-web-deploy/credentials/id_ed25519` целиком |
| `DEPLOY_KNOWN_HOSTS` | Содержимое `build/github-web-deploy/credentials/known_hosts` целиком |

Это отдельный ключ для kassa. Существующий ключ из mir-server ограничен командами серверной выкладки и не подходит для передачи клиентской сборки. Личный SSH-ключ в GitHub не загружается.

После установки доступа, добавления секретов и коммита workflow:

```bash
git push origin master
```

Состояние запуска: [Actions kassa](https://github.com/barbazan/kassa/actions).

## Что происходит на сервере

- Принимается архив по SSH; проверяются SHA-256, формат файлов и соответствие коммиту и номеру запуска GitHub. Пути за пределы папки, ссылки и слишком большие архивы отклоняются.
- Выкладка использует ту же блокировку, что и mir-server. Одновременные обновления ждут друг друга.
- Предыдущая версия сохраняется в `/home/codex/mir-server/.kassa-web-backups`.
- Файлы обновляются в `/home/codex/mir-server/static/kassa` с сохранением `.gitignore`.
- Проверяются опубликованные файлы по HTTPS. Если передача или проверка не удалась, предыдущая версия восстанавливается, а workflow остаётся неуспешным.
- Более старый запуск не заменяет уже выложенный новый запуск.

В `https://mir-game.ru/kassa/release.json` доступны SHA коммита, номер запуска и попытки. На сервере эти данные также хранятся в `.git/kassa-web-deployed.json`. Бэкапы не удаляются автоматически; старые копии можно очищать по мере необходимости.

Отдельный ключ имеет forced command `/usr/bin/python3 /home/codex/.local/lib/kassa-web-deploy/receive.py` и допускает только `check` и строго заданную команду выкладки. Произвольные команды SSH и SCP не разрешены. При изменении `scripts/receive-web-deploy.py` повторите установку через `setup-web-deploy.ps1 -Install`: сервер использует установленную копию вне checkout.

Для выкладки нужны `python3`, `rsync` и права пользователя `codex` на папку игры. Перезапуск nginx и Tomcat для обновления этих статических файлов не требуется. Код серверного биллинга обновляется существующим workflow mir-server.

## Проверка

```bash
python3 scripts/tests/test_web_deploy.py
```

Тесты работают во временной папке, без SSH и реальных платежей. Проверяются успешная выкладка, восстановление после неполной передачи и ошибки сайта, неверный хеш и коммит, опасные пути и ссылки, устаревшие запуски, повторная попытка и отсутствие файлов servlet-контейнера в веб-пакете.

Основа настройки: [GitHub Actions deployments](https://docs.github.com/en/actions/how-tos/deploy/configure-and-manage-deployments/control-deployments), [Actions secrets](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets).
