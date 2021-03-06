package com.special.blockduce.member.domain.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Valid
@ToString
public class SignupMemberRequest {

    @ApiModelProperty(required = true)
    @Email
    private String email;

    @ApiModelProperty(required = true)
    @NotNull
    @Pattern(regexp = "/^[가-힣]+$/")
    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    @Pattern(regexp = "/^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{6,20}$/")
    private String password;

    private String imageUrl;

    @ApiModelProperty(required = true)
    @NotNull
    private String nickname;

    @ApiModelProperty(required = true)
    @NotNull
    private String intro;



    public SignupMemberRequest() {
    }
    @Builder
    public SignupMemberRequest(@Email String email, @NotNull @Pattern(regexp = "/^[가-힣]+$/") String name, @NotNull @Pattern(regexp = "/^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{6,20}$/") String password, String imageUrl, @NotNull String nickname, @NotNull String intro) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.intro = intro;
    }
}
