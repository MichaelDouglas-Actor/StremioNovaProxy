# StremioNovaProxy

A tiny invisible Android TV app that sits between Stremio and Nova Video Player, fixing playback failures caused by long stream URLs (700+ characters).

## How it works

1. Registers itself as a video player so Stremio can target it
2. Receives the stream URL from Stremio (including long ones that normally fail)
3. Re-fires it to Nova Video Player — you never see any UI from this app

## Building (no Android Studio needed)

### Via GitHub Actions (recommended)

1. Create a free account at [github.com](https://github.com)
2. Create a new **public** repository named `StremioNovaProxy`
3. Upload all these project files (drag and drop works on GitHub's web UI)
4. Go to the **Actions** tab → **Build APK** → **Run workflow**
5. Wait ~3 minutes, then download the APK from the **Artifacts** section

### Via Android Studio (local)

1. Download [Android Studio](https://developer.android.com/studio)
2. Open this folder as a project
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Find the APK at `app/build/outputs/apk/release/app-release.apk`

## Installing on your Onn 4K Pro

1. **Settings → Device Preferences → Security & Restrictions** → enable Unknown Sources for your file manager
2. Transfer the APK to your Onn box (USB drive, or use a browser to download directly)
3. Install it via a file manager app (e.g. FX File Explorer)
4. In **Stremio → Settings → Player**, set the external player to **Nova Proxy**

## Notes

- The app has no icon and no UI — it is completely invisible
- It only acts when Stremio (or another app) sends it a video URL
- Nova Video Player must be installed for this to work
