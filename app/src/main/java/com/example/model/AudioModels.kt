package com.example.model

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "NEON PROTOCOL",
    val durationMs: Long = 252000L, // default 04:12
    val uriString: String = "",
    val codec: String = "FLAC",
    val spec: String = "96kHz/24bit",
    val bitrate: String = "1411 kbps LOSSLESS",
    val fileSizeStr: String = "54 MB",
    val crc32: String = "0x9FA8",
    val coverArtUrl: String = "",
    val isFavorite: Boolean = false,
    val isLocal: Boolean = false
) {
    val durationFormatted: String
        get() {
            val totalSecs = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSecs / 60
            val seconds = totalSecs % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}

data class TapeTrackItem(
    val index: String,
    val marker: String,
    val title: String,
    val soundchip: String,
    val duration: String
)

data class Mixtape(
    val id: String,
    val title: String,
    val subtitle: String,
    val tapeType: String,
    val biasBadge: String,
    val durationLabel: String,
    val curator: String,
    val coverArtUrl: String,
    val accentColorHex: Long,
    val tracks: List<TapeTrackItem>
)

data class DspPreset(
    val id: String,
    val name: String,
    val gains: List<Float> // 10 values for 31, 63, 125, 250, 500, 1k, 2k, 4k, 8k, 16k
)
