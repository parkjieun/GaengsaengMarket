<template>
<v-card width="300" height="400" class="mx-auto" id="chatRoom">
    <v-toolbar color="#bb99cd" flat dense>
        <v-toolbar-title style="font-size:0.9rem; " id="chatTitle" >
            <div @click="goUserProfile(patner.userId)" style="cursor: pointer;">
                <v-avatar size="30" color="#946da8">
                    <v-img  v-if="!!patner.profileImg" :src="patnerImg" />
                    <v-icon dark v-else>mdi-account</v-icon>
                </v-avatar>
                
                <span class="mx-3" style="color:#3d1860;">{{patner.nickName}}</span>
            </div>
            <v-icon small @click="closeRoom">mdi-close</v-icon>

        </v-toolbar-title>
    </v-toolbar>

    <v-list dense id="chatBody" v-if="messages.length">
        <v-list-item two-line v-for="item in messages" :key="item.messageId">
            <!-- 받는 메세지 -->
            <div v-if="item.userId != $store.state.myProfile.userId" class="row">
                <v-avatar size="30" style="margin-right: 5px;" color="#bb99cd" class="my-auto">
                    <v-img v-if="!!patner.profileImg" :src="patnerImg" />
                    <v-icon dark v-else>mdi-account</v-icon>
                </v-avatar>
                <v-list-item-content class="py-1">
                    <v-list-item-title class="receiveMsg my-auto caption delivery" v-if="isJsonString(item.content)">
                        <a data-tooltip-text="배송조회" @click="godeli(item.content)" v-text="deliveryLink(item.content)">

                        </a>
                    </v-list-item-title>
                    <v-list-item-title class="receiveMsg my-auto caption" v-else>{{item.content}}</v-list-item-title>

                    <v-list-item-subtitle style="width:150px; font-size:0.1rem; margin-left:10px;">{{item.createDate | processingDate}}</v-list-item-subtitle>
                </v-list-item-content>
            </div>
            <!-- 보내는 메세지 -->
            <div v-else class="row justify-end">
                <v-list-item-content class="py-1" style="width:auto; max-width:180px;">
                    <v-list-item-title class="sendMsg my-auto caption delivery" v-if="isJsonString(item.content)">
                        <a data-tooltip-text="배송조회" @click="godeli(item.content)" v-text="deliveryLink(item.content)">

                        </a>
                        </v-list-item-title>
                    <v-list-item-title class="sendMsg my-auto caption" v-else>{{item.content}}</v-list-item-title>
                    <v-list-item-subtitle style="font-size:0.1rem; text-align:right; margin-right:10px">{{item.createDate | processingDate}}</v-list-item-subtitle>
                </v-list-item-content>
                <v-avatar size="30" style="margin-right: 5px;" class="my-auto" color="#f5edf7">
                    <v-img v-if="!!myProfile.profileImg" :src="myProfileImg" />
                    <v-icon color="#3d1860" v-else>mdi-account</v-icon>
                </v-avatar>
            </div>
        </v-list-item>
    </v-list>
    <v-list dense id="chatBody" v-else>
        <v-list-item style="background: #f5edf7; font-size:0.7rem">
            <v-list-item-title style="color: #3d1860;">
                채팅 기록이 없습니다. <br>
                메세지를 보내 채팅을 시작하세요 :)
            </v-list-item-title>
        </v-list-item>
    </v-list>

    <v-divider></v-divider>
    <v-row class="chat-input" style="width:300px; margin:0; height:50px; max-height:50px;">
        <v-col cols="11" height="40">
            <v-text-field dense color="#643579" v-model="text" @keydown.enter="sendMessage" style="padding:0; width:100%;  font-size: 0.9rem;" placeholder="채팅내용을 입력해주세요"></v-text-field>
        </v-col>
        <v-col cols="1" height="40" style="padding: 10px 0;">
            <button @click="sendMessage" style="width:100% ">
                <v-icon small style="color:#3d1860; margin: 0;">
                    mdi-send
                </v-icon>
            </button>
        </v-col>
    </v-row>
    <delivery-detail :deliveryModal="deliveryModal" @closeModal="closeModal" />
</v-card>
</template>

<script>
import httpChat from "@/util/http-chat"
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import DeliveryDetail from '@/components/chat/DeliveryDetail.vue'
import axios from 'axios'
import {
    mapState
} from "vuex"
const baseURL = "http://i3a504.p.ssafy.io"
var t_key = "DlCHvPTEJjHmTu3U9IlZhA"
export default {
    props: ['roomId', 'showRoom', 'messages'],
    components: {
        DeliveryDetail,
    },
    data() {
        return {
            socket: null,
            stomp: null,
            userId: '',
            room: {},
            content: '',
            text: "",
            scroll: $("#chatBody").prop('scrollHeight'),
            deliveryModal: false,
        }
    },
    created() {
    },
    updated: function () {
        $("#chatBody").scrollTop($("#chatBody").prop('scrollHeight'))

    },
    watch: {
        showRoom() {
            if (this.showRoom) {
                $('#chatRoom').show();
            } else {
                $('#chatRoom').hide();
            }
        },
        isAuthenticated: function() {
            if (!this.isAuthenticated) {
                this.closeRoom()
            }
        }

    },
    computed: {
        ...mapState(['myProfile', 'patner', 'isAuthenticated']),
        patnerImg() {
            return baseURL + "/static/image/account/" + this.patner.profileImg
        },
        myProfileImg() {
            return baseURL + "/static/image/account/" + this.myProfile.profileImg 
        }
    },
    methods: {
        sendMessage: function () {
            if (this.text !== "") {
                this.stomp.send("/pub/chat/message/" + this.roomId, JSON.stringify({
                    userId: this.$store.state.myProfile.userId,
                    content: this.text
                }), {});
                this.text = '';
            }
        },
        recvMessage: function (recv) {
            this.messages.push({
                "userId": recv.userId,
                "content": recv.content,
                "createDate": recv.createDate
            })
        },
        connect(roomId) {
            this.socket = new SockJS("http://i3a504.p.ssafy.io:8001/ws-stomp");
            // this.socket = new SockJS("http://localhost:8001/ws-stomp");
            this.stomp = Stomp.over(this.socket);
            var recvMessage = this.recvMessage
            var stomp = this.stomp
            stomp.debug = () => {};
            this.stomp.connect({}, function (frame) {
                stomp.subscribe("/sub/chat/room/" + roomId, res => {
                    var recv = JSON.parse(res.body);
                    recvMessage(recv);
                });
            })
        },
        destroyed() {
            if (this.socket)
                this.socket.close()
        },
        itemHeight(item) {
            var i = document.getSelection(item)
            return i.style.height
        },
        closeRoom() {
            $('#chatRoom').hide();
            this.showRoom = false
            this.$emit('hideRoom', false)
        },
        deliveryLink(content) {
            const object = JSON.parse(content)
            return object.content
        },
        godeli(content) {
            const object = JSON.parse(content)
            axios.get(`http://info.sweettracker.co.kr/api/v1/trackingInfo?t_invoice=${object.content}&t_code=${object.code.replace("\"","").replace("\"","")}&t_key=${t_key}`)
                .then(res => {
                    this.$store.dispatch("setDelivery", res.data)
                    this.deliveryModal = true
                })
        },
        isJsonString(str) {
            try {
                var json = JSON.parse(str);
                return (typeof json === 'object');
            } catch (e) {
                return false;
            }
        },
        closeModal() {
            this.deliveryModal = false
        },
        goUserProfile(id) {
            this.$router.push({name: 'UserProfile', params: {uid: id}})
        },
    },
    filters: {
        processingDate(value) {
            const date = new Date(value)
            return (date.getMonth()+1) + "월 " + date.getDate() + "일  " + date.getHours() + "시 " + date.getMinutes() + "분";
        }
    },

}
</script>

<style scoped>
#chatTitle {
    width: 300px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

#chatBody {
    overflow-y: scroll;
    height: 300px;
}

.receiveMsg {
    background: #f5edf7;
    padding: 5px 5px 5px 10px;
    border-radius: 20px;
    color: #3d1860;
    white-space: pre-line;
    width: auto;
    max-width: 180px;
}

.sendMsg {
    float: right;
    background: #bb99cd;
    padding: 5px 5px 5px 10px;
    border-radius: 20px;
    color: #f5edf7;
    white-space: pre-line;
    width: auto;
    max-width: 180px;

}

.delivery a{
    color: rebeccapurple;
}
</style>
