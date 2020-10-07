import React from "react";
import { BaseRecycleBinAvatar } from "./style";

const RecycleBinAvatar = props => {
    return (
        <BaseRecycleBinAvatar lineColor='blueviolet' bgColor={BG_COLORS[props.type]}>
            {props.type}
        </BaseRecycleBinAvatar>
    );
};

const BG_COLORS = {
    glass: "yellow",
    paper: "red",
    metal: "greenyellow",
    plastic: "skyblue"
};

export default RecycleBinAvatar;