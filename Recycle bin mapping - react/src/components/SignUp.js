import React from "react";
import { Link } from "react-router-dom";
import ImageUploader from 'react-images-upload';
import Button from './Button';
import history from '../history';
import { BaseForm, InputHeader, BaseInput } from "./style";
import route from "../apis/route";
import Select from 'react-select';

class SignUp extends React.Component {
    
    state = {
        selectedRole: null
    }

    onClick = () => {
        const newUser = {
            'email': this.refs.email ? this.refs.email.value : '',
            'role': this.state.selectedRole ? this.state.selectedRole.value: null,
            'username': this.refs.username ? this.refs.username.value : '',
            'avatar': this.refs.avatar ? this.refs.avatar.state.pictures[0] : ''
        }
        route.post('/users', newUser, { HEADERS } )
        .then(res => {console.log(res.data)})
        .catch(err => {
            alert(err.response.data.message); 
            history.push('/new')
        });
    }

    onChangeRole = (selectedRole) => {
        this.setState({selectedRole});
    }

    render() {
        const options = [ { value: 'PLAYER', label: 'Player' }, { value: 'MANAGER', label: 'Manager' }];
        const customStyles = { 
            container: () => ({ width: 150}),
            menu: () => ({ width: 'auto', top: 'auto'})
        };
        const { selectedRole } = this.state;
        return (
            <BaseForm height='350px'>
                <InputHeader>Email:</InputHeader>
                <BaseInput ref='email' /*onChange={handleEmailChange}*/ />
                <InputHeader>Role:</InputHeader>
                {/* <Dropdown ref='role'/> */}
                <Select options={options} styles={customStyles} value={selectedRole} onChange={this.onChangeRole}/>
                <InputHeader>Username:</InputHeader>
                <BaseInput ref='username'/*onChange={handleUsernameChange}*/ />
                <InputHeader>Avatar:</InputHeader>
                <ImageUploader 
                    ref='avatar' /*onChange={handleAvatarChange}*/ 
                    withLabel={false}
                    withIcon={false}
                    buttonText='Choose Your Avatar'
                    singleImage={true}
                    maxFileSize={5242880}
                    imgExtension={['.jpg', '.png', '.gif', '.jpeg', '.jfif']}
                    fileContainerStyle={{ boxShadow:'none', padding: '0px', margin: '0px' }}
                    buttonStyles={{margin: '0px', padding: '6px 14px', backgroundColor: '#9e9b9b'}}
                />
                <Link to="/" style={{marginBlockStart:'20px', textDecoration: 'none'}} > 
                    <Button 
                        text='Sign Up'
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
    // const [email, setEmail] = useState("");
    // const [role, setRole] = useState("");
    // const [userName, setUserName] = useState("");
    // const [avatar, setAvatar] = useState("");
    
//     const handleEmailChange = e => {
//         setEmail(e.target.value);
//     }
    
//     const handleRoleChange = e => {
//         setRole(e.target.value);
//     }
    
//     const handleUsernameChange = e => {
//         setUserName(e.target.value);
//     }
    
//     const handleAvatarChange = e => {
//         setAvatar(e.target.value);
//     }
    
//     const handleSignUp = () => {
//         createUser({email, role, userName, avatar});
//         props.onSignUp(email, role, userName, avatar);
//     };


//     return (
//         <BaseSignUp>
//             <InputHeader>Email:</InputHeader>
//             <BaseInput onChange={handleEmailChange} />
//             <InputHeader>Role:</InputHeader>
//             <BaseInput onChange={handleRoleChange}/>
//             <InputHeader>Username:</InputHeader>
//             <BaseInput onChange={handleUsernameChange} />
//             <InputHeader>Avatar:</InputHeader>
//             <BaseInput onChange={handleAvatarChange} />
//             <Link style={{marginBlockStart:'20px', textDecoration: 'none'}} to="/new">
//                 <Button 
//                     text='Sign Up'
//                     onClick={handleSignUp}
//                     fontSize='16px' 
//                     borderColor='blueviolet'
//                     backgroundColor='blueviolet' 
//                     textColor='white'/>
//             </Link>
//         </BaseSignUp>
//     );
// };

const HEADERS = { 'Accept' : 'application/json', 'Content-Type': 'application/json' };

export default SignUp;

