#!/bin/sh
# Android 와 같은 저장소 루트의 local.properties 를 iOS 비밀값의 원본으로 쓴다.
# 빌드할 때마다 local.properties 에서 iosApp/iosApp/Secrets.plist(gitignore) 를 다시 만든다.
# 값이 없으면 빈 문자열로 넣는다 → 앱은 Supabase Stub sync, Amplitude 콘솔 출력으로 폴백한다.
set -eu

IOS_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PROPS="$IOS_DIR/../local.properties"
OUT="$IOS_DIR/iosApp/Secrets.plist"
KEYS="SUPABASE_URL SUPABASE_ANON_KEY AMPLITUDE_API_KEY"

# KEY=VALUE 한 줄을 읽는다. 같은 키가 여러 번 있으면 마지막 값을 쓰고, CR 과 뒤쪽 공백은 지운다.
read_prop() {
  [ -f "$PROPS" ] || return 0
  sed -n "s/^[[:space:]]*$1[[:space:]]*=[[:space:]]*//p" "$PROPS" | tr -d '\r' | tail -n 1 | sed 's/[[:space:]]*$//'
}

if [ ! -f "$PROPS" ]; then
  echo "warning: local.properties 가 없어 Secrets.plist 를 빈 값으로 만듭니다."
fi

TMP="$(mktemp)"
printf '%s\n' \
  '<?xml version="1.0" encoding="UTF-8"?>' \
  '<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">' \
  '<plist version="1.0"><dict/></plist>' > "$TMP"

for key in $KEYS; do
  value="$(read_prop "$key")"
  [ -n "$value" ] || echo "warning: local.properties 에 $key 가 없습니다."
  /usr/bin/plutil -insert "$key" -string "$value" "$TMP"
done

# 내용이 같으면 덮어쓰지 않는다 (리소스가 매번 다시 복사되지 않게).
if [ -f "$OUT" ] && cmp -s "$TMP" "$OUT"; then
  rm -f "$TMP"
else
  mv "$TMP" "$OUT"
fi
