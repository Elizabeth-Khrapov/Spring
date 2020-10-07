import React from "react";
import { Link } from "react-router-dom";
import history from '../history';
import Button from './Button';
import { BaseForm, InputHeader, BaseInput } from "./style";
import route from "../apis/route";

class Login extends React.Component {

    onClick = () => {
        const userEmail = this.refs.email.value ? this.refs.email.value : 'aaa' ;
        route.get(`/users/login/${userEmail}`)
        .then(res => {
            window.sessionStorage.setItem('userData', JSON.stringify(res.data));

            console.log(res.data);
            history.push(res.data.role !== 'PLAYER' ? "/" : "/map");
        })
        .catch(err => {
            alert(err.response.data.message);
            history.push('/login');
        });
            
    }

    render() {
        return (
            <BaseForm height='150px'>
                <InputHeader>Email:</InputHeader>
                <BaseInput ref='email' />
                <Link to='/' style={{marginBlockStart:'20px', textDecoration: 'none'}} > 
                    <Button 
                        text='Log In'
                        onClick={this.onClick}
                        fontSize='16px' 
                        borderColor='blueviolet'
                        backgroundColor='blueviolet' 
                        textColor='white'/>
                </Link>
            </BaseForm>
        );
    }
}

export default Login;