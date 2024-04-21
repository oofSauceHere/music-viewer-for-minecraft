# spotify-integration-mod
Creates a HUD element in the top left corner that shows what's currently playing on Spotify.

This isn't done but as of now it functions (on my computer). I'm working on making it useable for other people right now, but it may take a little as this is both my first foray into Minecraft modding *and* my first time retrieving data from a REST API. You'd think I'd have started with Javascript for the latter, but no. I didn't.

Though this is supposed to be a description of the project and how to install it, I figured I'd share the sources I used to figure out how to piece this together. Spotify has a great [web API documentation](https://developer.spotify.com/documentation/web-api) (which I'm sure most web developers know about), and [Kaupenjoe on YouTube](https://www.youtube.com/@ModdingByKaupenjoe) has useful tutorials on how to mod for most versions and mod loaders.

There's also an already existing mod that basically does what I did but with more functionality. It's called [Spoticraft](https://github.com/IMB11/Spoticraft), and I'd occasionally use the repo for reference. This project is entirely mine, though, because I wanted to learn and implement myself. One of the major differences is that I wrote the code to fetch data from Spotify's API myself, while they used an already existing library. Most normal people would choose to use that library, but because I wanted a more robust understanding of web APIs, I did it myself.
