import React from "react";
import Avatar from "./Avatar";
import Username from "./Username";
import Button from "./Button";
import { BaseNavbar, BaseGroup } from "./style";
import { Link } from "react-router-dom";


class Navbar extends React.Component {

    state = {
        user : JSON.parse(window.sessionStorage.getItem('userData'))
    }

    onLogout = () => {
        window.sessionStorage.clear('userData');
        this.intervalId = setInterval(this.updateUser, 500);
        this.setState({user : window.sessionStorage.getItem('userData')});
    }

    componentDidMount() {
        this.intervalId = setInterval(this.updateUser, 500);
    }

    componentWillUnmount(){
        clearInterval(this.intervalId);
    }

    updateUser = () => {
        if(window.sessionStorage.getItem('userData')) {
            clearInterval(this.intervalId);
            this.setState({user : JSON.parse(window.sessionStorage.getItem('userData'))});
        } else {
            this.setState({user : window.sessionStorage.getItem('userData')});
        }
    }

    render() {
    return (
        this.state.user ? 
        <BaseNavbar>
            <BaseGroup>
                <Link to='/profile'><Avatar img={this.state.user.avatar}/></Link> 
                <Username userName={this.state.user.username}/>
            </BaseGroup>
            {this.state.user.role === "MANAGER" ?
            <BaseGroup>
                <Link to='elements/new' style={{textDecoration: 'none'}}>
                    <Button text='Add Recycle Bin' fontSize='16px' borderColor='white' textColor='white'/>
                </Link>
                <Link to='/login' style={{textDecoration: 'none'}}>
                    <Button text='Logout' fontSize='16px' borderColor='white' textColor='white' onClick={this.onLogout}/>
                </Link>   
            </BaseGroup> :
                <Link to='/login' style={{textDecoration: 'none'}}>
                    <Button text='Logout' fontSize='16px' borderColor='white' textColor='white' onClick={this.onLogout}/>
                </Link>}
        </BaseNavbar> 
        :
        <BaseNavbar>
            <h1>Welcome!</h1>
            <BaseGroup>
                <Link to='/login' style={{textDecoration: 'none'}}>
                    <Button text='Login' fontSize='16px' borderColor='white' textColor='white'/>
                </Link>
                <Link to='/new' style={{textDecoration: 'none'}}>
                    <Button text='Sign Up' fontSize='16px' borderColor='white' textColor='white'/>
                </Link>
            </BaseGroup>
        </BaseNavbar>
    );
    }
}

export default Navbar;