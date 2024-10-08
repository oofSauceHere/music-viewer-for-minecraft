# music-viewer-for-minecraft
Creates a HUD element in the top left corner of the Minecraft screen that shows what's currently playing on Spotify. Implements authorization code OAUTH2 flow and handles requests to the Spotify API in Java using multithreading.

---

This project works, but still has some issues that need fixing. I'm working on making it useable for other people right now, but it may take a little as this is both my first foray into Minecraft modding *and* my first time retrieving data from a REST API. You'd think I'd have started with JavaScript for the latter, but no. I didn't.

Though this is traditionally a description of the project and how to install it, I figured I'd share the sources I used to figure out how to piece this together. Spotify has a great [web API documentation](https://developer.spotify.com/documentation/web-api) (which I'm sure most web developers know about), and [Kaupenjoe on YouTube](https://www.youtube.com/@ModdingByKaupenjoe) has useful tutorials on how to mod for most Minecraft versions and mod loaders.

There's also an already existing mod that has a similar functionality. It's called [Spoticraft](https://github.com/IMB11/Spoticraft), and I'd occasionally use the repo for reference. This project is entirely mine, though, because I wanted to learn and implement myself. One of the major differences is that I wrote the code to fetch data from Spotify's API myself, while they used an already existing library. Most normal people would choose to use that library, but because I ~am foolish~ wanted a more robust understanding of web APIs, I did it myself.
