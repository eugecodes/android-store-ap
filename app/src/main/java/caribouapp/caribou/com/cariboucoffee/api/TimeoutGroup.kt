package caribouapp.caribou.com.cariboucoffee.api

const val TIMEOUT_GROUP_HEADER = "TIMEOUT_GROUP"

enum class TimeoutGroup {
    Default,
    AddFunds;

    companion object {
        fun from(str: String?): TimeoutGroup {
            try {
                return TimeoutGroup.valueOf(str ?: Default.name)
            } catch (ex: IllegalArgumentException) {
                return Default
            }
        }
    }
}
