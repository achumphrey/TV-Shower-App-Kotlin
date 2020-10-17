package com.example.tvshowerappkotlin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tvshowerappkotlin.data.Image
import com.example.tvshowerappkotlin.data.TVShowerModel
import com.example.tvshowerappkotlin.network.TVShowerClient
import com.example.tvshowerappkotlin.network.TVShowerWebServices
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bytebuddy.implementation.MethodCall.call
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

  //  private var webServices: TVShowerWebServices = mock()
  //  private var webClient: TVShowerClient = mock()
    private var call: Call<TVShowerModel> = mock()
    private lateinit var data: TVShowerModel
    private lateinit var image: Image
    private var showName: String = " "

  //  @InjectMocks
    @Spy
    private var activity = MainActivity()

    @Spy
    private lateinit var webServices: TVShowerWebServices

    @Spy
    private lateinit var webClient: TVShowerClient


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        image = Image("anything")
        data = TVShowerModel(image, "anything", "anything")
        activity = MainActivity()
        webClient = TVShowerClient()
        webServices = webClient.retrofitInstance.create(TVShowerWebServices::class.java)
    }

    @Test
    fun fetchShow_ReturnData_WithSuccess() {
        showName = "girls"

        `when`(activity.webServices.getTVShow(showName)).thenReturn(call).getMock<TVShowerModel>()
        verify(activity, atLeast(1)).webServices.getTVShow(showName)
    }

    @Test
    fun fetchShow_NoReturnOfData_NullObject() {
        showName = "anything"
        this.webServices.getTVShow(showName)

    }

    @Test
    fun fetchShow_NoReturnData_NoNetwork() {
        showName = "girls"
        this.webServices.getTVShow(showName)

    }

    @Test
    fun fetchShow_NoReturnData_WithError() {
        showName = " "
        this.webServices.getTVShow(showName)

    }

    @After
    fun tearDown() {
    }
}