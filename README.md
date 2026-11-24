# Rage Launcher

**Rage Launcher** — a lightweight Android home launcher by **DevNameGelo**.

## Features in this first build
- Android HOME launcher activity (set it as the default Home app in system settings).
- Installed-app grid with app launch support.
- App search.
- Long-press an app to pin/unpin it in Favorites.
- Favorites persist locally.
- Dark black/crimson visual theme with SVG interface icons.
- Adaptive app grid for phones and tablets.
- Refreshes the installed-app list when the launcher returns to the foreground.
- No analytics, accounts, ads, network calls, or always-running service.

## Compatibility
- Minimum Android: 6.0 (API 23)
- Target SDK: 36
- Build JDK: 17

## Build
Open the project in Android Studio or run:

```bash
./gradlew :app:assembleDebug
```

Release build:

```bash
./gradlew :app:assembleRelease
```

If no signing secrets are configured, the release APK is signed with the standard debug key for testing. For updates distributed to users, configure one persistent signing key through GitHub Actions secrets:
- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

Do not change the signing key between releases, or Android will reject an in-place update.

## GitHub Actions
Push to `main` or `master`, or run **Build Rage Launcher APK** manually. The workflow uploads `Rage-Launcher-v1.0.0.apk` as an artifact.

## Notes
This launcher reduces its own overhead; Android does not expose a safe general-purpose API for third-party launchers to guarantee system-wide RAM cleanup, performance boosting, or battery stabilization. No fake booster is included.
