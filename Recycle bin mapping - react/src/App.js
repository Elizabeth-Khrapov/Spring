import React from "react";
import { Body, SlideShow } from "./style";
import Navbar from "./components/Navbar";
import Profile from "./components/Profile";
import Map from "./components/Map";
import SignUp from "./components/SignUp";
import history from './history';
import { Router, Switch, Route } from "react-router-dom";
import Login from "./components/Login";
import ElementCreate from "./components/ElementCreate";

const App = () => {

    return (
        <Body>
            <Router history={history}>
                <Navbar/>
                <Switch>
                    <Route exact path="/">
                        <SlideShow>
                            <li></li>
                            <li></li>
                            <li></li>
                        </SlideShow>
                    </Route>
                    <Route path="/map">
                        <Map />
                    </Route>
                    <Route path="/new" exact component={SignUp}/>
                    <Route path="/login" exact component={Login}/>
                    <Route path="/elements/new" exact component={ElementCreate}/>
                    <Route path="/profile">
                        <Profile />
                    </Route>
                </Switch>
            </Router>
        </Body>
    );
}

export default App;