package com.usepropeller.routable

import android.app.ListActivity
import android.os.Bundle
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RouterTest {

    private var _called: Boolean = false

    @Before
    @Throws(Exception::class)
    fun setUp() {
        this._called = false
    }

    @Test
    fun test_basic() {
        val router = Router()
        router.map("users/:user_id", ListActivity::class.java)

        val intent = router.intentFor("users/4")
        Assert.assertEquals("4", intent!!.extras.getString("user_id"))
    }

    @Test
    fun test_empty() {
        val router = Router()
        router.map("users", ListActivity::class.java)

        val intent = router.intentFor("users")
        Assert.assertNull(intent!!.extras)
    }

    @Test
    fun test_invalid_route() {
        val router = Router()
        var exceptionThrown = false

        try {
            router.intentFor("users/4")
        } catch (e: Router.RouteNotFoundException) {
            exceptionThrown = true
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail("Incorrect exception throw: " + e.toString())
        }

        Assert.assertTrue("Invalid route did not throw exception", exceptionThrown)
    }

    @Test
    fun test_invalid_context() {
        val router = Router()
        router.map("users", ListActivity::class.java)
        var exceptionThrown = false

        try {
            router.open("users")
        } catch (e: Router.ContextNotProvided) {
            exceptionThrown = true
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail("Incorrect exception throw: " + e.toString())
        }

        Assert.assertTrue("Invalid context did not throw exception", exceptionThrown)
    }

    @Test
    fun test_code_callbacks() {
        val router = Router(RuntimeEnvironment.application)
        router.map("callback", object : Router.RouterCallback() {
            override fun run(context: Router.RouteContext) {
                this@RouterTest._called = true

                Assert.assertNotNull(context.context)
            }
        })

        router.open("callback")

        Assert.assertTrue(this._called)
    }

    @Test
    fun test_code_callbacks_with_params() {
        val router = Router(RuntimeEnvironment.application)
        router.map("callback/:id", object : Router.RouterCallback() {
            override fun run(context: Router.RouteContext) {
                this@RouterTest._called = true
                Assert.assertEquals("123", context.params["id"])
            }
        })

        router.open("callback/123")

        Assert.assertTrue(this._called)
    }

    @Test
    fun test_code_callbacks_with_extras() {
        val router = Router(RuntimeEnvironment.application)
        router.map("callback/:id", object : Router.RouterCallback() {
            override fun run(context: Router.RouteContext) {
                this@RouterTest._called = true
                Assert.assertEquals("value", context.extras.getString("test"))
            }
        })

        val extras = Bundle()
        extras.putString("test", "value")

        router.open("callback/123", extras)

        Assert.assertTrue(this._called)
    }

    @Test
    fun test_url_starting_with_slash() {
        val router = Router()
        router.map("/users", ListActivity::class.java)

        val intent = router.intentFor("/users")
        Assert.assertNull(intent!!.extras)
    }

    @Test
    fun test_url_querystring() {
        val router = Router()
        router.map("/users/:id", ListActivity::class.java)

        val intent = router.intentFor("/users/123?key1=val2")
        val extras = intent!!.extras

        Assert.assertEquals("123", extras.getString("id"))
        Assert.assertEquals("val2", extras.getString("key1"))
    }

    @Test
    fun test_url_containing_spaces() {
        val router = Router()
        router.map("/path+entry/:id", ListActivity::class.java)

        val intent = router.intentFor("/path+entry/123")
        val extras = intent!!.extras

        Assert.assertEquals("123", extras.getString("id"))
    }

    @Test
    fun test_url_querystring_with_encoded_value() {
        val router = Router()
        router.map("/users/:id", ListActivity::class.java)

        val intent = router.intentFor("/users/123?key1=val+1&key2=val%202")
        val extras = intent!!.extras

        Assert.assertEquals("val 1", extras.getString("key1"))
        Assert.assertEquals("val 2", extras.getString("key2"))
    }

    @Test
    fun test_url_querystring_without_value() {
        val router = Router()
        router.map("/users/:id", ListActivity::class.java)

        val intent = router.intentFor("/users/123?val1")
        val extras = intent!!.extras

        Assert.assertFalse(extras.containsKey("val1"))
    }

    @Test
    fun test_url_starting_with_slash_with_params() {
        val router = Router()
        router.map("/users/:user_id", ListActivity::class.java)

        val intent = router.intentFor("/users/4")
        Assert.assertEquals("4", intent!!.extras.getString("user_id"))
    }
}