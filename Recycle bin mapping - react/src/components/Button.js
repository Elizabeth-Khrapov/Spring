import React from "react";
import { BaseButton } from "./style";

const Button = props => {
    return (
        <BaseButton
            onClick ={props.onClick}
            fontSize={props.fontSize}
            borderColor={props.borderColor}
            backgroundColor={props.backgroundColor}
            textColor={props.textColor}>
                {props.text}
        </BaseButton>
    );
};

export default Button;