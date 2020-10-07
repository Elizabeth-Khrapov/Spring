import React from "react";
import { BaseProfile, BigProfileImg, ProfileContent, ProfileHeader, ProfileInfo } from "./style";

class Profile extends React.Component {
    
    state = {
        user : JSON.parse(window.sessionStorage.getItem('userData'))
    }
    
    render() {
        return (
            <BaseProfile>
                <BigProfileImg src={this.state.user.avatar} />
                <ProfileContent>
                    <ProfileHeader>User Info</ProfileHeader>
                    <ProfileInfo>
                        Username: <b>{this.state.user.username}</b>
                    </ProfileInfo>
                    <ProfileInfo>
                        Role: <b>{this.state.user.role}</b>
                    </ProfileInfo>
                    <ProfileInfo>
                        Email: <b>{this.state.user.email}</b>
                    </ProfileInfo>
                </ProfileContent>
            </BaseProfile>
        );
    } 
};

export default Profile;