<template>
  <div class="marginTop">
    <link rel="preconnect" href="https://fonts.gstatic.com" />
    <link
      href="https://fonts.googleapis.com/css2?family=Black+Han+Sans&display=swap"
      rel="stylesheet"
    />
    <div class="user_Info">
      <b-img
        v-if="myInfo.profile_img === '' || myInfo.profile_img === null"
        class="profile_img"
        v-bind:src="
          'https://www.searchpng.com/wp-content/uploads/2019/02/Profile-PNG-Icon.png'
        "
        rounded="circle"
        alt="profile image"
      ></b-img>
      <b-img
        v-else
        class="profile_img"
        v-bind:src="myInfo.profile_img"
        rounded="circle"
        alt="profile image"
      ></b-img>

      <div class="text">
        {{ myInfo.name }}
      </div>
      <div class="text">
        {{ myInfo.nick }}
      </div>
      <div
        v-show="show_account === true"
        v-if="myInfo.account === '' || myInfo.account === null"
      >
        <b-button v-b-toggle.collapse-1 variant="outline-info" class="text"
          >지갑 생성</b-button
        >
        <b-collapse id="collapse-1" class="mt-2">
          <p class="card-text">
            경고! 지갑 비밀키를 잃어버리지 마세요! 한번 잃어버리면 복구 할 수
            없습니다.
          </p>
          <p class="card-text">
            공유하지 마세요! 비밀키가 악위적인 사이트에 노출되면 당신의 자산이
            유실될 수 있습니다.
          </p>
          <p class="card-text">
            백업을 만들어 두세요! 종이에 적어서 오프라인으로 관리하세요.
          </p>
          <b-button class="text" variant="warning" v-on:click="GetNewAccount"
            >확인</b-button
          >
        </b-collapse>
      </div>
      <div>
        <div>
          <div
            class="account_text"
            v-show="show_account === true"
            v-if="myInfo.account != '' && myInfo.account != null"
          >
            My Account : {{ myInfo.account }}
          </div>
          <button
            v-show="show_account === false"
            @click="[(show_account = true), GetMyAccount()]"
            class="corner-button"
          >
            <span> 지갑 주소 가져오기</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
let web3;
import * as Web3 from 'web3';
import axios from 'axios';
const userId = localStorage.getItem('id');

export default {
  data() {
    return {
      apiKey: '',
      balance: 0,
      show_account: false,
      myInfo: {
        name: '',
        nick: '',
        profile_img: '',
        account: '',
        balance: '0.00 ',
        ERCbalance: '0.00 ',
      },
      contract_result: '',
      privateKey: '',
    };
  },
  created() {
    console.log('ready!');
    if (typeof web3 !== 'undefined') {
      console.log('Web3 Detected! ' + web3.currentProvider.constructor.name);
      web3 = new Web3(web3.currentProvider);
    } else {
      console.log('No Web3 detected .. using HTTP provider');
      const projectId = 'b04025a46bb245b3bdb7c350a938dbe5';
      web3 = new Web3(
        new Web3.providers.HttpProvider(
          `https://ropsten.infura.io/v3/${projectId}`
        )
      );
    }
    async function getData(myInfo) {
      try {
        const response = await axios.get(
          'http://j4b107.p.ssafy.io/api/members/' + userId
        );
        if (response) {
          myInfo.name = response.data.nickname;
          myInfo.nick = response.data.intro;
          myInfo.profile_img = response.data.img;
          myInfo.balance = response.data.eth;
          myInfo.ERCbalance = response.data.dbc;
        } else {
          console.log('Waiting for user data ');
        }
      } catch (error) {
        console.log(error);
      }
    }
    getData(this.myInfo);
  },
  methods: {
    GetMyAccount: function() {
      async function getMyAccount(myInfo) {
        try {
          const response = await axios.get(
            'http://j4b107.p.ssafy.io/api/members/' + userId + ''
          );
          if (response) {
            myInfo.account = response.data.account;
          } else {
            console.log('Waiting for user data ');
          }
        } catch (error) {
          console.log(error);
        }
      }
      getMyAccount(this.myInfo);
    },

    GetNewAccount: function() {
      let result = web3.eth.accounts.create('newAccount');
      axios
        .put('http://j4b107.p.ssafy.io/api/election/createAccount/' + userId, {
          account: result.address,
          eth: 0,
          dbc: 0,
          key: result.privateKey,
        })
        .then(() => {
          this.myInfo.account = result.address;
          axios
            .get(
              'http://j4b107.p.ssafy.io/api/election/EthReward/' +
                userId +
                '/33000000'
            )
            .then(() => {})
            .catch((error) => {
              console.log(error);
            });
        })
        .catch((error) => {
          console.log(error);
        });
      alert('개인 키 ' + result.privateKey);
      window.location.reload();
    },
  },
};
</script>

<style scoped>
@font-face {
  font-family: 'account_font';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts-20-12@1.0/SDSamliphopangche_Outline.woff')
    format('woff');
  font-weight: normal;
  font-style: normal;
}
.marginTop {
  margin-top: 50px;
}
.text {
  font-family: 'account_font';
  font-size: x-large;
}
.account_text {
  font-family: 'account_font';
  font-size: large;
  overflow-wrap: anywhere;
}
.corner-button {
  font-family: 'account_font';
  letter-spacing: 0.2rem;
  cursor: pointer;
  background: transparent;
  border: 0.2rem solid currentColor;
  padding: 0.6rem 0.1rem;
  font-size: 1rem;
  color: #0c3267;
  position: relative;
  transition: color 0.3s;
}
.corner-button:hover {
  color: #eeca99;
}
.corner-button:hover::before {
  width: 0;
}
.corner-button:hover::after {
  height: 0;
}
.corner-button:active {
  border-width: 0.25rem;
}
.corner-button span {
  position: relative;
  z-index: 2;
}
.corner-button::before,
.corner-button::after {
  content: '';
  position: absolute;
  background: #f5f7fa;
  z-index: 1;
  transition: all 1s;
}
.corner-button::before {
  width: calc(100% - 2rem);
  height: calc(101% + 1rem);
  top: -0.5rem;
  left: 50%;
  transform: translateX(-50%);
}
.corner-button::after {
  height: calc(100% - 2rem);
  width: calc(101% + 1rem);
  left: -0.5rem;
  top: 50%;
  transform: translateY(-50%);
}
.profile_img {
  width: 30%;
  height: 30%;
}
</style>
