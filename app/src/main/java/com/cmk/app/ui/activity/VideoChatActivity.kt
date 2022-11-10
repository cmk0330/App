package com.cmk.app.ui.activity

import android.os.Bundle
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityVideoChatBinding
import com.cmk.app.ext.loge
import com.dylanc.viewbinding.binding
import io.agora.rtm.*

class VideoChatActivity : BaseActivity() {

    val AGORA_APP_ID = "ab29054e559c44668a2f63c493091e0c"
    val token ="007eJxTYFh77qu826U5nedlLq6JumAeY3nrY8IM3d5y5kvbVEMNWhkVGBKTjCwNTE1STU0tk01MzMwsEo3SzIyTTSyNDSwNUw2SOf7lJDcEMjK8jkhiZWSAQBCfgyE5IzEvLzXHkYEBAObgIJg="

    private val binding by binding<ActivityVideoChatBinding>()
    private lateinit var rtmClient: RtmClient
    private var rtmChannel: RtmChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRtm()
        binding.loginButton.setOnClickListener {
            rtmLogin()
        }
        binding.joinButton.setOnClickListener {
            join()
        }
        binding.logoutButton.setOnClickListener {
            rtmLogout()
        }
        binding.sendPeerMsgButton.setOnClickListener {
            sendPeerMsg()
        }
        binding.sendChannelMsgButton.setOnClickListener {
            sendChannelMsg()
        }
    }

    fun initRtm() {
        rtmClient = RtmClient.createInstance(this, AGORA_APP_ID, object : RtmClientListener {
            override fun onConnectionStateChanged(p0: Int, p1: Int) {
                "onConnectionStateChanged:$p0,$p1".loge()
            }

            override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
                "onMessageReceived:${p0?.messageType},$p1"
            }

            override fun onTokenExpired() {
                "onTokenExpired".loge()
            }

            override fun onTokenPrivilegeWillExpire() {
                "onTokenPrivilegeWillExpire".loge()
            }

            override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                "onPeersOnlineStatusChanged".loge()
            }

        })
    }

    fun rtmLogin() {
        rtmClient.login(token, binding.uid.text.toString(), object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "声望登录成功".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "声望登录失败$p0".loge()
            }
        })
    }

    /**
     * 加入频道
     */
    fun join() {
        val channelName = binding.channelName.text.toString()
        rtmChannel = rtmClient.createChannel(channelName, object : RtmChannelListener {
            override fun onMemberCountUpdated(p0: Int) {
                "onMemberCountUpdated:$p0".loge()
            }

            override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
                "onAttributesUpdated:${p0.toString()}".loge()
            }

            override fun onMessageReceived(p0: RtmMessage?, p1: RtmChannelMember?) {
                "onMessageReceived:${p0?.text},${p1.toString()}".loge()
            }

            override fun onMemberJoined(p0: RtmChannelMember?) {
                "onMemberJoined:userId->${p0?.userId},channelId->${p0?.channelId}"
            }

            override fun onMemberLeft(p0: RtmChannelMember?) {
                "onMemberLeft:userId->${p0?.userId},channelId->${p0?.channelId}"
            }
        })
        rtmChannel?.join(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "成功加入频道".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "加入频道失败${p0.toString()}".loge()
            }
        })
    }

    private fun rtmLogout() {
        rtmClient.logout(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "声望退出登录".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "声望退出登录失败$p0".loge()
            }
        })
    }

    /**
     * 离开频道
     */
    private fun leave() {
        rtmChannel?.leave(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "已经离开频道".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "离开频道出错${p0.toString()}".loge()
            }
        })
    }

    /**
     * 发送点对点消息按钮
     */
    private fun sendPeerMsg() {
        val msg = binding.msgBox.text.toString()
        val objectName = binding.peerName.text.toString()
        val message = rtmClient.createMessage()
        message.text = msg
        val options = SendMessageOptions()
        rtmClient.sendMessageToPeer(objectName, message, options, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "已发送点对点消息".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "点对点消息发送失败${p0.toString()}".loge()
            }
        })
    }

    /**
     * 发送频道消息
     */
    private fun sendChannelMsg() {
        val msg = binding.msgBox.text.toString()
        val message = rtmClient.createMessage()
        message.text = msg
        val options = SendMessageOptions()
        rtmChannel?.sendMessage(message, options, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                "已发送频道消息".loge()
            }

            override fun onFailure(p0: ErrorInfo?) {
                "发送频道消息失败${p0.toString()}".loge()
            }
        })
    }

}