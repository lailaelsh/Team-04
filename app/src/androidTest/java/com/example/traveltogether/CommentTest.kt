package com.example.traveltogether

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class AddComment {
    private lateinit var loginUser: LoginUser
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var firebaseRef: DatabaseReference
    private var string = "test comment"
    private lateinit var pid : String
    private lateinit var userPost : UserPost

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun checkLogin () {
        loginUser = LoginUser("test@gmail.com", "Name", "12345678", "")
        loginUser.signIn()
        firebaseDb = FirebaseDatabase.getInstance()
        firebaseRef = firebaseDb.reference
        var list : MutableList<Comment> = mutableListOf()
        firebaseRef.child("posts").push().
        setValue(UserPost(
            FirebaseAuth.getInstance().currentUser?.uid.toString(), "1", System.currentTimeMillis(),
            "Delete Test", "Malle", 1, 1,
            3, "hallo", list))
        var data = Tasks.await(firebaseRef.child("posts").get())


        for (item in data.children) {
            assert(item.hasChild("title"))
            if (item.child("title").value.toString() == "Delete Test" &&
                item.child("uid").value.toString() ==
                FirebaseAuth.getInstance().currentUser?.uid
            ) {
                pid = item.key.toString()
                break
            }
        }
        assert(pid != "")

        val dataNew = Tasks.await(firebaseRef.child("posts").child(pid).get())

        userPost = UserPost(dataNew.child("uid").value.toString(),
            dataNew.key, dataNew.child("timePosted").value as Long, dataNew.child("title").value.toString(),
            dataNew.child("destination").value.toString(),
            dataNew.child("startDate").value as Long,
            dataNew.child("endDate").value as Long,
            dataNew.child("numOfPeople").value as Long,
            dataNew.child("description").value.toString(), list)
    }

    @After
    fun cleanup () {
        userPost.delete()
    }

    @Test
    fun functionality() {

        firebaseRef.child("posts").child(pid).child("comments").push()
            .setValue(Comment(string, if (FirebaseAuth.getInstance().currentUser?.displayName == "") "Anonymous"
            else FirebaseAuth.getInstance().currentUser?.displayName , System.currentTimeMillis()))

        val data_ = Tasks.await(firebaseRef.child("posts").child(pid).child("comments").get())
        for (comment in data_.children) {
            assert(comment.child("comment").value == string)
        }
    }

    @Test
    fun checkDisplay() {
        onView(withId(R.id.saved_post_fragment)).perform(click())
        onView(withText("Comments")).perform(click())


        onView(withId(R.id.enter_comment_field)).check(matches(isDisplayed()))
        onView(withId(R.id.button_comment_send)).check(matches(isDisplayed()))
        Thread.sleep(4000)
        onView(withText("Comments")).check(matches(isDisplayed()))

    }

    @Test
    fun testCommentDisplay() {
        onView(withId(R.id.saved_post_fragment)).perform(click())
        onView(withText("Comments")).perform(click())

        onView(withId(R.id.enter_comment_field)).perform(typeText(string))
        onView(withId(R.id.button_comment_send)).perform(click())
        Thread.sleep(1000)
        onView(withText(string)).check(matches(isDisplayed()))

    }
    @Test
    fun checkDisplay2() {
        onView(withId(R.id.all_post_fragment)).perform(click())
        onView(withText("Comments")).perform(click())
        //onView(withText("Comments")).check(matches(isDisplayed()))
    }

    @Test
    fun showmoreButton() {
        onView(withId(R.id.saved_post_fragment)).perform(click())
        onView(withText("Comments")).perform(click())
        onView(withText("show more")).check(matches(isDisplayed()))

        //onView(withId(R.id.enter_comment_field)).perform(typeText(string))
        //onView(withId(R.id.button_comment_send)).perform(click())
        Thread.sleep(1000)
        //onView(withText(string)).check(matches(isDisplayed()))

    }


}