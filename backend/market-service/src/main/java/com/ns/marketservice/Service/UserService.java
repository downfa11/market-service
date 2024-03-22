package com.ns.marketservice.Service;

import com.ns.marketservice.Domain.DTO.*;
import com.ns.marketservice.Domain.Membership;
import com.ns.marketservice.Repository.UserRepository;
import com.ns.marketservice.Utils.jwtToken;
import com.ns.marketservice.Utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final JwtTokenProvider jwtTokenProvider;


    public MyDataMembership myDataMembershipShow() {
        Long idx = jwtTokenProvider.getMembershipIdbyToken();

        Optional<Membership> memberOptional = repository.findById(idx);

        if (memberOptional.isPresent()) {
            Membership membership = memberOptional.get();
            return DecryptMyDataMembership(membership);

        } else {
            throw new RuntimeException("Invalid LoginRequest");
        }
    }

    @Transactional
    public MyDataMembership DecryptMyDataMembership(Membership membership){

        MyDataMembership Mydata = new MyDataMembership();
        Mydata.setMembershipId(membership.getMembershipId());

        Mydata.setName(membership.getName());
        Mydata.setNickname(membership.getNickname());
        Mydata.setAddress(membership.getAddress());
        Mydata.setEmail(membership.getEmail());

        //String decryptedName = vaultAdapter.decrypt(membership.getName());
        //String decryptedAddress = vaultAdapter.decrypt(membership.getAddress());
        //String decryptedEmail = vaultAdapter.decrypt(membership.getEmail());

        Mydata.setRegion(membership.getRegion());
        Mydata.setExp(membership.getExp());
        Mydata.setLevel(membership.getLevel());
        Mydata.setType(membership.getType());

        return Mydata;
    }

    public RegisterMembershipResponse RegisterMembership(RegisterMembershipRequest request){
        if(repository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("already exists email");

        if (repository.findByNickname(request.getNickname()).isPresent())
            throw new RuntimeException("already exists nickname");


        try {

            String name = request.getName();
            String address = request.getAddress();
            String email = request.getEmail();
            //String encryptedName = vaultAdapter.encrypt(name);
            //String encryptedAddress = vaultAdapter.encrypt(address);
            //String encryptedEmail = vaultAdapter.encrypt(email);

            Membership membership = EncryptMembership(name,request.getNickname(),address,email,request.getRegion(),request.isValid());
            Long userid = membership.getMembershipId();
            return new RegisterMembershipResponse(userid,
                    membership.getName(),
                    membership.getNickname(),
                    membership.getEmail(),
                    membership.getAddress(),
                    membership.getRegion());
        }
        catch (Exception e){
            throw new RuntimeException("registerMembership error: "+e);
        }
    }

    @Transactional
    public Membership EncryptMembership(String encryptedName,String nickname,String encryptedEmail,String encryptedAddress,String region,Boolean isValid){
        Membership membership = Membership.builder()
                .membershipId((long) UUID.randomUUID().hashCode())
                .name(encryptedName)
                .nickname(nickname)
                .email(encryptedEmail)
                .address(encryptedAddress)
                .region(region)
                .role(0).exp(0).level(1).type("")
                .curProductRegion("")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .isValid(isValid)
                .build();

        repository.save(membership);
        return membership;
    }
    public boolean deleteByMembership(Long memberId){
        try {
            Optional<Membership> memberOptional = repository.findById(memberId);
            if (memberOptional.isPresent()) {
                Membership member = memberOptional.get();

                repository.delete(member);
                return true;
            }

        }  catch(Exception e){
                throw new RuntimeException("deleteMembership error: "+e); }
        return false;
    }
    public jwtToken LoginMembership(String email) {

        Optional<Membership> memberOptional = repository.findByEmail(email);
        if(memberOptional.isPresent()){
            Membership membership = memberOptional.get();

            if(membership.isValid()){
                Long id = membership.getMembershipId();
                String jwt = jwtTokenProvider.generateJwtToken(id);
                String refreshToken = jwtTokenProvider.generateRefreshToken(id);
                membership.setRefreshToken(refreshToken);
                repository.save(membership);

                return jwtToken.generateJwtToken(
                        new jwtToken.MembershipId(id.toString()),
                        new jwtToken.MembershipJwtToken(jwt),
                        new jwtToken.MembershipRefreshToken(refreshToken)
                );
            }
        }

        //register
        return null;
    }


    public jwtToken LoginMembershipTemp(LoginMembershipRequest request) {
        Long membershipId = request.getMembershipId();
        Optional<Membership> memberOptional = repository.findById(request.getMembershipId());
        if(memberOptional.isPresent()){
            Membership membership = memberOptional.get();

            if(membership.isValid()){

                String token = jwtTokenProvider.generateJwtToken(membershipId);

                String refreshToken = jwtTokenProvider.generateRefreshToken(membershipId);

                membership.setRefreshToken(refreshToken);

                return jwtToken.generateJwtToken(
                        new jwtToken.MembershipId(membershipId.toString()),
                        new jwtToken.MembershipJwtToken(token),
                        new jwtToken.MembershipRefreshToken(refreshToken)
                );

            }
        }

        return null;
    }
    public jwtToken refreshJwtToken(RefreshTokenRequest request) {
        String RequestedRefreshToken = request.getRefreshToken();
        boolean isValid = jwtTokenProvider.validateJwtToken(RequestedRefreshToken);

        if(isValid){
            Long membershipId = jwtTokenProvider.parseMembershipIdFromToken(RequestedRefreshToken);

            Optional<Membership> memberOptional = repository.findById(membershipId);
            if(memberOptional.isPresent()){
                Membership membership = memberOptional.get();
                if(!membership.getRefreshToken().equals(request.getRefreshToken()))
                    return null;

            if(membership.isValid()){
                String newJwtToken = jwtTokenProvider.generateJwtToken(membershipId);

                return jwtToken.generateJwtToken(
                        new jwtToken.MembershipId(membershipId.toString()),
                        new jwtToken.MembershipJwtToken(newJwtToken),
                        new jwtToken.MembershipRefreshToken(RequestedRefreshToken)
                );
            }
            }
        }
        return null;
    }

    public boolean validateJwtToken(ValidateTokenRequest request) {

        String token = request.getJwtToken();
        return jwtTokenProvider.validateJwtToken(token);
    }

    public Membership getMembershipByJwtToken(ValidateTokenRequest request) {
        String token = request.getJwtToken();
        boolean isValid = validateJwtToken(request);

        if (isValid) {
            Long membershipId = jwtTokenProvider.parseMembershipIdFromToken(token);

            Optional<Membership> memberOptional = repository.findById(membershipId);
            if(memberOptional.isPresent()){
                Membership membership = memberOptional.get();
             if (!membership.getRefreshToken().equals(request.getJwtToken())) return null;

            return membership;
            }
        }
        return null;
    }

}
