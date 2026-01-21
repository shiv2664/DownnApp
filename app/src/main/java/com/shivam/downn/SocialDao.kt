package com.shivam.downn

import com.google.gson.annotations.SerializedName

data class SocialDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("locationName") val locationName: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("participantIds") val participantIds: List<String>,
    @SerializedName("maxParticipants") val maxParticipants: Int?
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("interests") val interests: List<String>,
    @SerializedName("createdAt") val createdAt: Long
)

data class Social(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val title: String,
    val description: String?,
    val category: SocialCategory,
    val location: Location,
    val timestamp: Long,
    val participants: List<String>,
    val maxParticipants: Int?,
    val distance: Double? = null // Calculated distance from user
) {
    val timeAgo: String get() {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> "${diff / 86400_000}d ago"
        }
    }
}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String
)

enum class SocialCategory(val displayName: String, val emoji: String) {
    TRAVEL("Travel", "✈️"),
    PARTY("Party", "🎉"),
    FOOD("Food", "🍔"),
    EVENTS("Events", "🎭"),
    HOBBY("Hobby", "🎨"),
    SPORTS("Sports", "⚽"),
    OTHER("Other", "📌")
}

data class SocialEntity(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val title: String,
    val description: String?,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val timestamp: Long,
    val participantIds: String, // JSON string of list
    val maxParticipants: Int?,
    val cachedAt: Long = System.currentTimeMillis()
)

object DummyData {

    // Sample avatar URLs (using placeholder service)
    private val avatarUrls = listOf(
        "https://i.pravatar.cc/150?img=1",
        "https://i.pravatar.cc/150?img=2",
        "https://i.pravatar.cc/150?img=3",
        "https://i.pravatar.cc/150?img=4",
        "https://i.pravatar.cc/150?img=5",
        "https://i.pravatar.cc/150?img=6",
        "https://i.pravatar.cc/150?img=7",
        "https://i.pravatar.cc/150?img=8",
        "https://i.pravatar.cc/150?img=9",
        "https://i.pravatar.cc/150?img=10"
    )

    data class SocialItem(
        val id: String,
        val userName: String,
        val userAvatar: String?,
        val moveTitle: String,
        val description: String?,
        val category: String,
        val categoryEmoji: String,
        val timeAgo: String,
        val distance: String,
        val participantCount: Int,
        val maxParticipants: Int?,
        val participantAvatars: List<String>,
        val socialType: com.shivam.downn.data.models.SocialType = com.shivam.downn.data.models.SocialType.PERSONAL
    )

    val dummySocials = listOf(
        SocialItem(
            id = "1",
            userName = "Priya Sharma",
            userAvatar = avatarUrls[0],
            moveTitle = "Exploring Hauz Khas Village tonight! 🌙",
            description = "Looking for cool people to check out the cafes and art galleries. Let's make it a vibe!",
            category = "Travel",
            categoryEmoji = "✈️",
            timeAgo = "5m ago",
            distance = "2.3 km away",
            participantCount = 4,
            maxParticipants = 8,
            participantAvatars = listOf(avatarUrls[1], avatarUrls[2], avatarUrls[3], avatarUrls[4])
        ),
        SocialItem(
            id = "2",
            userName = "Rohan Mehta",
            userAvatar = avatarUrls[1],
            moveTitle = "Club hopping in Cyber Hub 🎉",
            description = "Anyone down to hit up some clubs tonight? Got a crew of 3, looking for more fun people!",
            category = "Party",
            categoryEmoji = "🎉",
            timeAgo = "12m ago",
            distance = "5.8 km away",
            participantCount = 6,
            maxParticipants = 12,
            participantAvatars = listOf(
                avatarUrls[5],
                avatarUrls[6],
                avatarUrls[7],
                avatarUrls[8],
                avatarUrls[9],
                avatarUrls[0]
            )
        ),
        SocialItem(
            id = "3",
            userName = "Ananya Gupta",
            userAvatar = avatarUrls[2],
            moveTitle = "Late night momos at Majnu ka Tilla?",
            description = "Craving some good Tibetan food. Who's in?",
            category = "Food",
            categoryEmoji = "🍔",
            timeAgo = "23m ago",
            distance = "1.2 km away",
            participantCount = 2,
            maxParticipants = 6,
            participantAvatars = listOf(avatarUrls[3], avatarUrls[4])
        ),
        SocialItem(
            id = "4",
            userName = "Arjun Singh",
            userAvatar = avatarUrls[3],
            moveTitle = "Trekking to Nag Tibba this weekend",
            description = "Planning a 2-day trek. Need experienced trekkers. Leaving Friday evening from Delhi.",
            category = "Travel",
            categoryEmoji = "✈️",
            timeAgo = "1h ago",
            distance = "3.5 km away",
            participantCount = 5,
            maxParticipants = 10,
            participantAvatars = listOf(
                avatarUrls[5],
                avatarUrls[6],
                avatarUrls[7],
                avatarUrls[8],
                avatarUrls[9]
            )
        ),
        SocialItem(
            id = "5",
            userName = "Sneha Patel",
            userAvatar = avatarUrls[4],
            moveTitle = "Stand-up comedy show at Canvas Laugh Club",
            description = "Got an extra ticket! Show starts at 8 PM. Let's share an Uber too?",
            category = "Events",
            categoryEmoji = "🎭",
            timeAgo = "2h ago",
            distance = "4.1 km away",
            participantCount = 1,
            maxParticipants = 2,
            participantAvatars = listOf(avatarUrls[0])
        ),
        SocialItem(
            id = "6",
            userName = "Kabir Malhotra",
            userAvatar = avatarUrls[5],
            moveTitle = "Sunday football at JNU grounds ⚽",
            description = "We have 8 players, need 3 more for a proper match. All skill levels welcome!",
            category = "Sports",
            categoryEmoji = "⚽",
            timeAgo = "3h ago",
            distance = "6.2 km away",
            participantCount = 8,
            maxParticipants = 11,
            participantAvatars = listOf(
                avatarUrls[1],
                avatarUrls[2],
                avatarUrls[3],
                avatarUrls[4],
                avatarUrls[5],
                avatarUrls[6],
                avatarUrls[7],
                avatarUrls[8]
            )
        ),
        SocialItem(
            id = "7",
            userName = "Diya Kapoor",
            userAvatar = avatarUrls[6],
            moveTitle = "Photography walk at India Gate 📸",
            description = "Golden hour shoot tomorrow morning. Bring your cameras and let's capture some magic!",
            category = "Hobby",
            categoryEmoji = "🎨",
            timeAgo = "4h ago",
            distance = "7.8 km away",
            participantCount = 3,
            maxParticipants = 8,
            participantAvatars = listOf(avatarUrls[9], avatarUrls[0], avatarUrls[1])
        ),
        SocialItem(
            id = "8",
            userName = "Vikram Reddy",
            userAvatar = avatarUrls[7],
            moveTitle = "Board game night at my place 🎲",
            description = "Got Catan, Codenames, and more. BYOB. Let's have a chill evening!",
            category = "Hobby",
            categoryEmoji = "🎨",
            timeAgo = "5h ago",
            distance = "2.9 km away",
            participantCount = 4,
            maxParticipants = 8,
            participantAvatars = listOf(avatarUrls[2], avatarUrls[3], avatarUrls[4], avatarUrls[5])
        ),
        SocialItem(
            id = "9",
            userName = "Meera Joshi",
            userAvatar = avatarUrls[8],
            moveTitle = "Brunch at SodaBottleOpenerWala",
            description = "Solo traveler here! Looking for people to grab brunch with. Love trying new places!",
            category = "Food",
            categoryEmoji = "🍔",
            timeAgo = "6h ago",
            distance = "3.7 km away",
            participantCount = 2,
            maxParticipants = 6,
            participantAvatars = listOf(avatarUrls[6], avatarUrls[7])
        ),
        SocialItem(
            id = "10",
            userName = "Aditya Chopra",
            userAvatar = avatarUrls[9],
            moveTitle = "Rishikesh rafting trip next weekend! 🌊",
            description = "Planning camping + rafting. 2 nights, 3 days. Cost around ₹3500 per person. Adventure seekers only!",
            category = "Travel",
            categoryEmoji = "✈️",
            timeAgo = "8h ago",
            distance = "1.8 km away",
            participantCount = 7,
            maxParticipants = 15,
            participantAvatars = listOf(
                avatarUrls[0],
                avatarUrls[1],
                avatarUrls[2],
                avatarUrls[3],
                avatarUrls[4],
                avatarUrls[5],
                avatarUrls[6]
            )
        ),
        SocialItem(
            id = "11",
            userName = "Tanya Verma",
            userAvatar = avatarUrls[0],
            moveTitle = "Sunset at Lodhi Garden 🌅",
            description = "Just moved to Delhi. Would love to explore with some friendly faces!",
            category = "Travel",
            categoryEmoji = "✈️",
            timeAgo = "10h ago",
            distance = "4.5 km away",
            participantCount = 1,
            maxParticipants = 5,
            participantAvatars = listOf(avatarUrls[8])
        ),
        SocialItem(
            id = "12",
            userName = "Rahul Bhatia",
            userAvatar = avatarUrls[1],
            moveTitle = "Open mic poetry night at Cha Bar",
            description = "Performing some of my work tonight. Come support! Starts at 7:30 PM.",
            category = "Events",
            categoryEmoji = "🎭",
            timeAgo = "12h ago",
            distance = "5.3 km away",
            participantCount = 9,
            maxParticipants = null, // No limit
            participantAvatars = listOf(
                avatarUrls[2],
                avatarUrls[3],
                avatarUrls[4],
                avatarUrls[5],
                avatarUrls[6],
                avatarUrls[7],
                avatarUrls[8],
                avatarUrls[9],
                avatarUrls[0]
            )
        ),
        SocialItem(
            id = "13",
            userName = "Simran Kaur",
            userAvatar = null, // No avatar example
            moveTitle = "Yoga session at Nehru Park 🧘‍♀️",
            description = "Free morning yoga class tomorrow at 6 AM. All levels welcome. Bring your own mat!",
            category = "Sports",
            categoryEmoji = "⚽",
            timeAgo = "15h ago",
            distance = "2.1 km away",
            participantCount = 12,
            maxParticipants = 20,
            participantAvatars = listOf(
                avatarUrls[1],
                avatarUrls[2],
                avatarUrls[3],
                avatarUrls[4],
                avatarUrls[5]
            )
        ),
        SocialItem(
            id = "14",
            userName = "Nikhil Desai",
            userAvatar = avatarUrls[2],
            moveTitle = "Movie marathon: Marvel Phase 4 🎬",
            description = "Starting at noon Saturday. Got popcorn and snacks covered. Just bring yourself!",
            category = "Events",
            categoryEmoji = "🎭",
            timeAgo = "18h ago",
            distance = "8.2 km away",
            participantCount = 3,
            maxParticipants = 8,
            participantAvatars = listOf(avatarUrls[6], avatarUrls[7], avatarUrls[8])
        ),
        SocialItem(
            id = "15",
            userName = "Ishita Nair",
            userAvatar = avatarUrls[3],
            moveTitle = "Book club meet: Discussing 'Midnight's Children'",
            description = "Monthly book club. Coffee and discussion at Kunzum Travel Cafe this Sunday 4 PM.",
            category = "Hobby",
            categoryEmoji = "🎨",
            timeAgo = "1d ago",
            distance = "6.7 km away",
            participantCount = 6,
            maxParticipants = 12,
            participantAvatars = listOf(
                avatarUrls[9],
                avatarUrls[0],
                avatarUrls[1],
                avatarUrls[2],
                avatarUrls[3],
                avatarUrls[4]
            )
        ),
        SocialItem(
            id = "16",
            userName = "The Daily Grind",
            userAvatar = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=150",
            moveTitle = "Live Jazz Night 🎷",
            description = "Join us for a chill evening of live jazz and 20% off all brews!",
            category = "Food",
            categoryEmoji = "☕️",
            timeAgo = "Just now",
            distance = "0.5 km away",
            participantCount = 45,
            maxParticipants = 100,
            participantAvatars = emptyList(),
            socialType = com.shivam.downn.data.models.SocialType.BUSINESS
        ),
        SocialItem(
            id = "17",
            userName = "Club Social",
            userAvatar = "https://images.unsplash.com/photo-1566737236500-c8ac1f852382?w=150",
            moveTitle = "Friday Night Fever 🕺",
            description = "The biggest party in town. Special discount for groups of 4!",
            category = "Party",
            categoryEmoji = "🎉",
            timeAgo = "2h ago",
            distance = "1.2 km away",
            participantCount = 120,
            maxParticipants = 500,
            participantAvatars = emptyList(),
            socialType = com.shivam.downn.data.models.SocialType.BUSINESS
        )
    )
}
