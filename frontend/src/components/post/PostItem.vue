<template>
<div style="cursor:pointer;">

    <v-card tile width="250px;" height="350" style="position:relative; min-width:250px;max-width:250px;">

      <div v-if="post.type===1">
        <v-img
        :src="'http://i3a504.p.ssafy.io/static/image/post/'+post.files.split(',')[0]"
        height="200px"
        ></v-img>
      </div>
      <div v-else style="position: relateve;">
        
        <v-img
        :src="'http://i3a504.p.ssafy.io/static/image/post/'+post.files.split(',')[0]"
        height="200px" style="filter:brightness(50%);"
        ></v-img>
        <h4 style="position:absolute; top:90px; color:white; width:250px; background:black; padding:5px; opacity: 0.8;">판매완료</h4>
      </div>
        <v-divider></v-divider>
        <v-card-title class="py-1" style="text-overflow:ellipsis;white-space:nowrap;word-wrap:normal;width:100%;overflow:hidden;">
          <h5 style="width:100%; color:#3d1860;" class="card-title text-left" >
            {{post.title}}
            </h5>
        </v-card-title >

        <v-card-subtitle class="py-2 text-left" style="height:24px;overflow:hidden;padding-bottom:5px; margin-bottum: 5px;">
           <v-icon color="#3d1860" x-small>mdi-currency-krw</v-icon><span style="color:#3d1860; font-weight:bold;">{{post.price | currency}}</span>
        </v-card-subtitle>
        <v-card-text class="text-left" style="padding-bottom:0px;padding-top:5px;margin-top:5px; height: 60px; padding-left: 10px;">
          <v-chip style="margin-right:3px;margin-top:3px;pointer-events: none !important;" color="#f5edf7" text-color="#643579"  small v-for="tag in tags" :key="tag">
              {{ tag }}
            </v-chip>
        </v-card-text>
      <v-card-actions style="position:absolute; bottom: 0; width:250px; display:flex; justify-content:space-between; height:40px;">
          <div>
          <v-icon color="red" small class="mx-2">mdi-heart</v-icon><span class="caption"> {{post.like_cnt}} </span>
          </div>
          <div style="">
            <v-chip class="my-1" small label v-if="post.deal_type !== 2" color="#e7d8eb" style="color:#3d1860; font-weight:bold; margin-right:5px;pointer-events: none !important;">택배</v-chip>            
            <v-chip class="my-1" small label v-if="post.deal_type !== 1" color="#e7d8eb" style="color:#3d1860; font-weight:bold; margin-right:5px;pointer-events: none !important;">직거래</v-chip>
          </div>
      </v-card-actions>
    </v-card>

</div>
</template>

<script>
export default {
    props: ['post'],
    data(){
      return{
        tags:[],
      }
    },
    created(){
      // console.log(`http://i3a504.p.ssafy.io:8000/api/static/image/+post.files.split(',')[0]`)
      
    },
    mounted(){
      this.tags = this.post.tags.split(",").slice(0,3)
    },
    filters: {
      currency: function (value) {
          var num = new Number(value);
          return num.toFixed(0).replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, "$1,")
      },
      watch:{
        post(val){
          console.log(this.tags)
        }
    },

  }
}
</script>

<style scoped>
.card-title{
  text-overflow:ellipsis;
  white-space:nowrap;
  word-wrap:normal;
  width:100px;
  overflow:hidden;
}
/* .v-chip{
  pointer-events: none !important;
} */
.v-card{
	transition: transform .2s ease-in-out;
}
.v-card:hover{
  transform: scale(1.05);

  z-index:98;
}
</style>

