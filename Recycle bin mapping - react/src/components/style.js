import styled from "styled-components";

export const BaseNavbar = styled.div`
    display: flex;
    justify-content: space-between;
    flex-direction: row;
    align-items: center;
    height: 50px;
    z-index: 1;
    position: relative;
    text-align: center;
    background-color: blueviolet;
    h1 {
        color: white;
        margin-inline-start: 40px;
    }
`;

export const BaseAvatar = styled.img`
    margin-inline-start: 10px;
    display: flex;
    border: 1px solid white;
    border-radius: 50px;
    width: 40px;
    height: 40px;
`;

export const BaseUsername = styled.div`
    margin-inline-start: 10px;
    font-size: 24px;
    color: white;
`;

export const BaseButton = styled.div`
    display: flex;
    width: auto;
    height: auto;
    padding: 8px;
    font-size: ${props => props.fontSize};
    justify-content: center;
    align-items: center;
    margin-inline-end: 10px;
    border: 1px solid ${props => props.borderColor};
    border-radius: 50px;
    background-color: ${props => props.backgroundColor};
    color: ${props => props.textColor};
    cursor: pointer;
    text-decoration: none;
`;

export const BaseGroup = styled.div`
    display: flex;
    flex-direction: row;
    align-items: center
`;

export const RecycleBinMarker = styled.div`
    display: flex;
    background-color: ${props => props.bgColor};
    justify-content: center;
    align-items: center;
    border: 1px solid blueviolet;
    color: blueviolet;
    border-radius: 50px;
    width: 30px;
    padding: 4px;
    height: 30px;
`;

export const BaseRecycleBinAvatar = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    height: 75px;
    width: 75px;
    padding: 14px;
    border-radius: 60px;
    border: 3px solid ${props => props.lineColor};
    font-size: 35px;
    color: ${props => props.lineColor};
    background-color: ${props => props.bgColor};
    margin-inline-start: 20px;
    margin-inline-end: 20px;
`;

export const BaseRecycleBinInfo = styled.div`
    display: ${props => props.display};
    position: absolute;
    justify-content: flex-start;
    flex-direction: row;
    border-top: 1px solid blueviolet;
    align-items: center;
    height: 120px;
    width: 100vw;
    background: white;
    z-index: 2;
`;

export const BaseRecycleBinDetails = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 260px;
    margin-inline-end: 10px;
    padding-block-end: 10px;
`;

export const BaseForm = styled.div`
    display: flex;
    margin-top: 100px;
    flex-direction: column;
    height: ${props => props.height};
    justify-content: space-between;
    align-items: center;
`;

export const InputHeader = styled.div`
    font-size: 16px;
`;

export const BaseInput = styled.input`
    width: 170px;
    margin-block-start: 5px;
    padding: 5px 10px 5px 10px;
    color: blueviolet;
    text-align: center;
    border-radius: 50px;
    border: 1px solid blueviolet;
    height: 14px;
    font-size: 15px;
    &:focus {
        outline: none;
    }
`;

export const MapWrapper = styled.div`
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    height: calc(100vh - 50px);
    width: auto;
`;

export const BaseProfile = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: blueviolet;
`;

export const BigProfileImg = styled.img`
    border: 4px solid white;
    height: 250px;
    width: 250px;
    margin-block-start: 35px;
    margin-block-end: 40px;
    border-radius: 50%;
`;

export const ProfileContent = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    height: calc(100vh - 400px);
    width: 100vw;
    background-color: white;
`;

export const ProfileHeader = styled.div`
    margin-block-start: 10px;
    margin-block-end: 5px;
    font-size: 35px;
    color: blueviolet;
`;

export const ProfileInfo = styled.div`
    margin-block-start: 7px;
    font-size: 15px;
    color: blueviolet;
`;

export const FilterButton = styled.div`
    display: flex;
    position: absolute;
    justify-content: center;
    align-items: center;
    border-radius: 20px;
    color: blueviolet;
    padding-block-end: 10px;
    font-size: 21px;
    align-self: center;
    width: auto;
    z-index: 1;
`;

export const Filter = styled.button`
    display: flex;
    justify-content: center;
    font-size: 13px;
    margin-inline-start: 3px;
    margin-inline-end: 3px;
    background-color: ${props => props.backgroundColor};
    align-items: center;
    padding: 12px 15px 12px 15px;
    border: 2px solid blueviolet;
    border-radius: 30px;
`;

export const BaseText = styled.div`
    margin-block-start: 10px;
    font-size: 18px;
`;
