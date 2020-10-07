import React from 'react';
import { BaseForm, InputHeader, BaseInput } from './style';
import route from '../apis/route';
import { Link } from 'react-router-dom';
import history from '../history';
import Button from './Button';
import Select from 'react-select';

class ElementCreate extends React.Component {

    state = {
        user: JSON.parse(window.sessionStorage.getItem('userData')),
        selectedBinType: null,
    }

    onClick = () => {
        const elementType = 'recyclebin';
        const managerEmail = this.state.user ? this.state.user.email : 'aaa';
        const lat = this.refs.lat ? Number(this.refs.lat.value) : null;
        const lng = this.refs.lng ? Number(this.refs.lng.value) : null;
        const newElement = {
            'type': elementType,
            'name': this.state.selectedBinType ? this.state.selectedBinType.value : '',
            'active': true,
            'location': { lat, lng }
        }

        route.post(`/elements/${managerEmail}`, newElement, { HEADERS } )
        .then(res => {
            console.log(res.data);
            alert(res.data.name + ' recycle bin is added');
        })
        .catch(err => {
            alert(err.response.data.message);
            history.push('/elements/new');
        });
    }

    onChangeType = (selected) => {
        this.setState({selectedBinType : selected});
    }

    render() {
        const { selectedType } = this.state;
        const options = [ 
            { value: 'glass', label: 'Glass' }, 
            { value: 'paper', label: 'Paper' }, 
            { value: 'metal', label: 'Metal'},
            { value: 'plastic', label: 'Plastic'}
        ];
        const selectCustomStyles = { 
            container: () => ({ width: 150, marginBlockEnd: 10, marginBlockStart: 10}),
            menu: () => ({ width: 'auto', top: 'auto'})
        };

        return (
            <BaseForm height='250px'>
                <InputHeader>Type:</InputHeader>
                {/* <Dropdown ref='type'/> */}
                <Select options={options} styles={selectCustomStyles} value={selectedType} onChange={this.onChangeType}/>
                {/* <div style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
                    <InputHeader>Active:</InputHeader>
                    <Checkbox checked={true} onChange={this.onChangeActive} color='primary'/>
                </div> */}
                <InputHeader>Lat:</InputHeader>
                <BaseInput ref='lat'/>
                <InputHeader>Lng:</InputHeader>
                <BaseInput ref='lng'/>
                <Link to="/" style={{marginBlockStart:'20px', textDecoration: 'none'}} > 
                    <Button 
                        text='Add'
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

const HEADERS = { 'Accept' : 'application/json', 'Content-Type': 'application/json' };

export default ElementCreate;