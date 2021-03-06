package com.special.blockduce.member.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.special.blockduce.Response;
import com.special.blockduce.config.UserRole;
import com.special.blockduce.member.domain.Member;
import com.special.blockduce.member.domain.Salt;
import com.special.blockduce.member.dto.MemberForm;
import com.special.blockduce.member.dto.ProfileDto;
import com.special.blockduce.member.repository.MemberRepository;
import com.special.blockduce.utils.SaltUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public String getAccessToken(String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=360325f103f39664cd6c418590ff659c");
            sb.append("&redirect_uri=http://j4b107.p.ssafy.io/kakaologin");
            sb.append("&code=" + authorize_code);
            sb.append("&client_secret=Rrnt5OxvySpvbaiodqjg1Qt8BOA1QYUU");
            bw.write(sb.toString());
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public ProfileDto getUserInfo(String access_Token) {
        ProfileDto userInfo = new ProfileDto();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            JsonObject propertie = element.getAsJsonObject().get("properties").getAsJsonObject();

            String id = element.getAsJsonObject().get("id").getAsString();
            String name = propertie.getAsJsonObject().get("nickname").getAsString();
            String img = propertie.getAsJsonObject().get("profile_image").getAsString();
            String email = null;
            if (kakao_account.getAsJsonObject().get("email") != null) {
                email = kakao_account.getAsJsonObject().get("email").getAsString();
                userInfo = new ProfileDto(id,email,name,img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @Transactional
    public void join(MemberForm form) {

        String salt = SaltUtil.genSalt();

        Member member = Member.builder().
                email(form.getEmail()).
                name(form.getName()).
                password(SaltUtil.encodePassword(salt,form.getPassword())).
                kid(form.getKid()).
                ismem(form.getIsmem()).
                nickname(form.getNickname()).
                intro(form.getIntro()).
                img(form.getImg()).
                role(UserRole.USER).
                build();

        memberRepository.save(member);
    }

    @Transactional
    public MemberForm findByKid(ProfileDto userinfo) {

        Optional<Member> member = memberRepository.findOptionalByKid(userinfo.getId());

        MemberForm memf;
        if(member.isPresent()){ //????????? ?????? ??????
            memf =  MemberForm.builder().
                    kid(userinfo.getId()).
                    email(userinfo.getEmail()).
                    name(userinfo.getName()).
                    ismem(member.get().getIsmem()).
                    build();
        }else{
            memf =  MemberForm.builder().
                    kid(userinfo.getId()).
                    email(userinfo.getEmail()).
                    name(userinfo.getName()).
                    build();
        }
        return memf;
    }


    @Transactional
    public MemberForm findById(Long memberId) {
        MemberForm memf =new MemberForm();
        Member mem = Member.builder().ismem(false).build();

        // ????????? ???????????? member??? ?????? ???????????? ismem false??? ????????? ??????...
        Member member = memberRepository.findOptionalById(memberId).orElse(mem);


            if (member.getIsmem()==true) { //????????? ?????? ??????
                memf = MemberForm.builder().
                        id(member.getId()).
                        kid(member.getKid()).
                        email(member.getEmail()).
                        img(member.getImg()).
                        name(member.getName()).
                        nickname(member.getNickname()).
                        intro(member.getIntro()).
                        account(member.getAccount().getAccount()).  // ????????? ???????????? ???????????? ????????? ?????????? ?????? ????????? ??????
                        eth(member.getAccount().getEth()).
                        dbc(member.getAccount().getDbc()).
                        ismem(member.getIsmem()).
                        build();
            } else {
                memf = MemberForm.builder().ismem(false).build(); //?????? ?????? ?????? -> ???????????? ??????
            }


//        }else if(form.getEth()!=null && form.getDbc()==null){ //eth ??????
//            if (member.getIsmem()==true) { //????????? ?????? ??????
//                member.getAccount().updateEth(form.getEth());
//            } else {
//                memf = MemberForm.builder().ismem(false).build(); //?????? ?????? ?????? -> ???????????? ??????
//            }
//
//
//        }else if(form.getEth()==null && form.getDbc()!=null){//dbc ??????
//            if (member.getIsmem()==true) { //????????? ?????? ??????
//                member.getAccount().updateDbc(form.getDbc());
//            } else {
//                memf = MemberForm.builder().ismem(false).build(); //?????? ?????? ?????? -> ???????????? ??????
//            }
//        }
        return memf;
    }

    public MemberForm findByEmail(String email) {
        MemberForm memf;
        Member mem = Member.builder().ismem(false).build();
        // ????????? ???????????? member??? ?????? ???????????? ismem false??? ????????? ??????...
        Member member = memberRepository.findOptionalByEmail(email).orElse(mem);

        memf = MemberForm.builder().
                id(member.getId()).
                build();

        return memf;
    }
    @Transactional
    public String refreshDbc(Long memberId, Double dbc) {
        MemberForm memf = new MemberForm();
        Member mem = Member.builder().ismem(false).build();

        // ????????? ???????????? member??? ?????? ???????????? ismem false??? ????????? ??????...
        Member member = memberRepository.findOptionalById(memberId).orElse(mem);

                if (member.getIsmem() == true) { //????????? ?????? ?????? dbc ????????????
                member.getAccount().updateDbc(dbc);
                return "dbc ????????? ??????";
            } else {
                    return "????????? ??????";
            }

    }
    @Transactional
    public String refreshEth(Long memberId, Double eth) {

        Member mem = Member.builder().ismem(false).build();

        // ????????? ???????????? member??? ?????? ???????????? ismem false??? ????????? ??????...
        Member member = memberRepository.findOptionalById(memberId).orElse(mem);

        if (member.getIsmem() == true) { //????????? ?????? ?????? dbc ????????????
            member.getAccount().updateEth(eth);
            return "eth ????????? ??????";
        } else {
            return "????????? ??????";
        }
    }
}

