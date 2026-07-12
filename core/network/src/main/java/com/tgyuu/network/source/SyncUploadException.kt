package com.tgyuu.network.source

class SyncUploadException(
    table: String,
    rows: List<*>,
    cause: Throwable,
) : Exception(
    "Supabase upload failed [table=$table, rows=${rows.size}, " +
        "payload=${rows.toString().take(PAYLOAD_LOG_LIMIT)}] cause=${cause.message}",
    cause,
) {
    private companion object {
        private const val PAYLOAD_LOG_LIMIT = 2000
    }
}
