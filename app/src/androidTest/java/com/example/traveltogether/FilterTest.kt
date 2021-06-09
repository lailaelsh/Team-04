package com.example.traveltogether

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class FilterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var loginUser: LoginUser

    @Before
    fun setup() {
        loginUser = LoginUser("markus123@gmail.com", "Markus", "markus123", "Hallo")
        loginUser.signIn()
        onView(withId(R.id.all_post_fragment)).perform(click())
    }

    @Test
    fun checkFilter(){

        onView(withId(R.id.filter_button_expand)).perform(click())
        onView(withId(R.id.filter_title)).perform(typeText("hi"))
        onView(withId(R.id.filter_destination)).perform(typeText("graz"))
        onView(withHint(R.string.starting_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withText("hi")).check(matches(isDisplayed()))
    }
}