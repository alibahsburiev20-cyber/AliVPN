# AliVPN v0.1 — установка с Android без ПК

## 1. Скачать проект
Открой ZIP из чата и сохрани `AliVPN_v0_1.zip` в папку `Download` на телефоне.

## 2. Подготовить Termux
```bash
pkg update -y
pkg install git gh unzip -y
termux-setup-storage
```
Разреши Termux доступ к файлам.

## 3. Распаковать проект
```bash
cd ~/storage/downloads
unzip -o AliVPN_v0_1.zip -d ~
cd ~/AliVPN_v0_1
```

## 4. Войти в GitHub
```bash
gh auth login
```
Выбери:
- GitHub.com
- HTTPS
- Login with a web browser

Termux покажет одноразовый код. Открой страницу входа GitHub, введи код и подтверди доступ. Вернись в Termux.

## 5. Создать репозиторий и отправить файлы
```bash
gh repo create AliVPN --public --source=. --remote=origin --push
```
После этого проект будет лежать в твоём GitHub-репозитории `AliVPN`.

## 6. Запустить сборку APK
Открой репозиторий GitHub → вкладка **Actions** → workflow **Build AliVPN APK** → **Run workflow** → **Run workflow**.

После окончания зелёной сборки: открой запуск workflow → блок **Artifacts** → `AliVPN-debug-apk` → скачай ZIP → распакуй его → внутри будет `app-debug.apk`.

## 7. Установить APK
Открой `app-debug.apk`. Android может попросить разрешить установку из этого источника — разреши для браузера/файлового менеджера, которым открываешь APK. Затем установи AliVPN.

## 8. Первый запуск
1. Запусти **AliVPN**.
2. Нажми **CONNECT**.
3. Приложение загрузит список конфигураций.
4. Android покажет системное окно создания VPN-соединения. Нажми **Разрешить**.
5. Статус станет **ПОДКЛЮЧЕНО**.

## Важно про v0.1
На этом этапе приложение действительно создаёт системный Android VPN-интерфейс, но ещё не прогоняет интернет-трафик через выбранный публичный VLESS/VMess/Trojan-узел. Следующий этап — подключить полноценное ядро прокси/VPN и автоматический выбор реально работающего узла.
