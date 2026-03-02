🌤️ ClimaTrack
ClimaTrack is an Android mobile application that provides real‑time weather updates, forecasts, and customizable alerts. Users can view weather conditions for their current location, search and save favorite places, and receive notifications for extreme weather events.

📱 Features
Settings Screen
- Choose location via GPS or map picker
- Select temperature units (Kelvin, Celsius, Fahrenheit)
- Select wind speed units (m/s, mph)
- Switch app language (Arabic / English)
Home Screen
- Current temperature, date, time, humidity, wind speed, pressure, clouds, city name
- Weather icon + description (e.g., clear sky, light rain)
- Hourly history for the current day
- 5‑day forecast
Weather Alerts Screen
- Create alerts for rain, wind, extreme temperatures, fog, snow, etc.
- Configure duration, notification type (silent, sound), and stop/cancel options
Favorites Screen
- Save favorite locations with map marker or autocomplete search
- View full forecast for each saved location
- Add/remove favorites easily with a Floating Action Button

🛠️ Tech Stack
- Architecture: MVVM + Repository pattern
- Networking: Retrofit (OpenWeatherMap API)
- Database: Room (offline caching of forecasts & favorites)
- Concurrency: Kotlin Coroutines
- Background Tasks: WorkManager (alerts & notifications)
- UI: Jetpack Compose (minimal, centered, responsive design)

🌍 API
Weather data powered by OpenWeatherMap Forecast API.
