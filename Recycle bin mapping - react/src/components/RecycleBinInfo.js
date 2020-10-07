import React from "react";
import RecycleBinAvatar from "./RecycleBinAvatar";
import { BaseRecycleBinInfo, BaseRecycleBinDetails, BaseText } from "./style";

const RecycleBinInfo = props => {
    
    return (props.data ?
        <BaseRecycleBinInfo display={DISPLAY[props.isVisible]}>
            <RecycleBinAvatar type={props.data.name} />
            <BaseRecycleBinDetails>
                 <BaseText>{"id: " +props.data.elementId}</BaseText>
                 <BaseText>{"lat: " + props.data.location.lat}</BaseText>
                 <BaseText>{"lng: " + props.data.location.lng}</BaseText>
            </BaseRecycleBinDetails>
        </BaseRecycleBinInfo> 
        : null
    );
};

const DISPLAY = {
    yes: "flex",
    no: "none"
};

export default RecycleBinInfo;