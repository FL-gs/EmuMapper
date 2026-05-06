  <img src="./assets/main.gif" width="800"/>

---

# EmuCtrlr

EmuCtrlr is an Android app designed for Android handheld gaming devices such as Retroid or AYN consoles.

It automatically manages controller assignments for emulators, enabling a seamless handheld ↔ docked experience.
The app is designed for controller-based navigation and does not support touchscreen input.
<p align="center">

> [!IMPORTANT]
> EmuCtrlr was originally made for personal use.
> I am currently refactoring it to support more devices, controllers, and emulator setups.
>
> Bugs may occur, and the app may not work correctly on every device or setup yet.


> [!WARNING]
> DualSense controllers are not supported yet.


</p>

## Table of Contents

- [How It Works](#how-it-works)
  - [Controller Detection](#controller-detection)
  - [Config Writing](#config-writing)
- [Emulator Compatibility](#emulator-compatibility)
- [Important Emulator Behavior](#important-emulator-behavior)
- [First Setup](#first-setup)
- [Modes](#modes)
    - [Manual Mode](#manual-mode)
    - [Automatic Mode](#automatic-mode)
- [Settings](#settings)
- [Performance](#performance)
- [Troubleshooting](#troubleshooting)

---

## How It Works

EmuCtrlr works in two steps:

1. It detects the connected controllers.
2. It writes the correct controller order into supported emulator config files.

### Controller Detection

EmuCtrlr scans connected controllers:

- when the app starts
- when a controller is connected
- when a controller is disconnected
- when the controller order changes

Controllers are assigned to player slots based on their connection order.

When external controllers are connected, EmuCtrlr switches Player 1 from the built-in controls to the first external controller.

When all external controllers are disconnected, EmuCtrlr switches Player 1 back to the built-in controls.

### Config Writing

After detecting the controller order, EmuCtrlr can write the updated configuration into enabled emulator config files.

The write behavior depends on the selected mode:

- In **Manual Mode**, open EmuCtrlr and hold **Start** with Player 1 to write the controller configuration.
- In **Automatic Mode**, EmuCtrlr writes the configuration automatically whenever the controller state changes.

Only enabled emulators are modified.
Disabled emulators are not affected.

<img src="./assets/main_connected.png" width="800"/>

---

## Emulator Compatibility

If you want me to test other standalone emulators, feel free to contact me. This list should cover most common use cases.

| Emulator     | Status | Notes                                                                                |
|-------------|--------|--------------------------------------------------------------------------------------|
| Eden        | ✅     | Fully working                                                                        |
| Dolphin     | ✅     | GameCube controls only                                                               |
| RetroArch   | ⚠️     | Works, but player slot assignment is currently bugged                                |
| DuckStation | ❌ | Requires root access to config files. Use Swanstation on RetroArch as an alternative |
| AetherSX2   | ❌     | Requires root access to config files                                                 |
| NetherSX2   | ❌     | Requires root access to config files                                                 |

> [!NOTE]
> For RetroArch, EmuCtrlr uses the priority option to match player order with controller connection order.
> However, this RetroArch feature is currently bugged.
>
> Until this is fixed in RetroArch, it is recommended to disable config writing for RetroArch.

---
## Important Emulator Behavior

If an emulator is already running, restart it for the changes to take effect.

This happens because most emulators load their controller configuration only when they start. EmuCtrlr cannot force an emulator to reload its config while a game is already running.

For a smoother docked experience, you can use a keymapper shortcut to close the current game or emulator directly from your controller.


## First Setup

<img src="./assets/onboarding.png" width="800"/>

On first launch, EmuCtrlr needs to identify which controller profile belongs to the handheld built-in controls.

1. Launch the app
2. Select the built-in controller profile (it may appear as "Xbox Wireless Controller" or other names).
3. Enable the emulators you want EmuCtrlr to manage

> [!IMPORTANT]
> Because some Android handheld devices use proxy controller profiles, it is recommended to turn off external controllers before selecting the built-in profile.

---

## Modes
You can change the config writing mode from the app Settings.
### Manual Mode

Manual mode gives full control to the user.

You open the app and press **Start** (Player 1 only) when you want to write the controller configuration.

This mode is recommended if you do not want the app running in the background.

### Automatic Mode

Automatic mode works like plug and play.

The app automatically writes the correct configuration whenever the controller state changes:

- controller connected
- controller disconnected
- controller order changed

Automatic mode runs as a foreground service, allowing the app to continuously monitor controller state and update configurations in real time, even when the app is in the background.

Use this mode if you want a fully automated experience without manually triggering configuration updates.

For RAM usage while running in the background, see the RAM usage in the Performance section below.

---

## Settings

<img src="./assets/settings1.png" width="800"/>

### Theme

Change the app appearance.

### Auto / Manual Mode

Choose whether the app writes configs manually or automatically.

### Built-in Controller

Select the controller profile used by the handheld built-in controls.

### Emulators

Enable or disable config writing for each detected emulator.

Only enabled emulators will be modified. Disabled emulators will not be affected.

### Debug

Enable debug logs when troubleshooting an issue.

---

## Performance

### RAM Usage During Config Writing

<img src="./assets/memory_usage.png" width="800"/>

During normal idle usage, the app uses around **80 MB of RAM**.

During config writing, RAM usage can temporarily increase by around **8 MB**, then returns to the normal idle value.


### 24h Memory Test

![24h memory graph](./assets/ram-24h.png)

A 24-hour memory test shows stable RAM usage with no visible memory leak.

---

## Troubleshooting

### Missing emulator

If an emulator is installed but not detected, please contact me.

This can happen with different package variants, such as:

- x64 builds
- nightly builds
- forks
- custom package names

### Missing controller or console visuals

Some controller or console visuals may be missing.

If this happens, please contact me with the device/controller name.

### Controller mapping issue

The app uses a classic controller layout.

If buttons are incorrectly assigned, please contact me with:

- device name
- controller name
- emulator used
- description of the issue

### Config writing issue

If config writing does not work:

1. Open Settings
2. Enable debug logs
3. Reproduce the issue
4. Send the debug log file  