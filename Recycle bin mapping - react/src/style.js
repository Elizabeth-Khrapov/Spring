import styled from "styled-components";

export const Body = styled.div`
    font-family: Roobert TRIAL;
    user-select: none;
`;

export const SlideShow = styled.ul`
    height: calc(100%-50px);
    position: fixed;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 0;
    list-style: none;
    margin: 0;
    padding: 0;
      
    li { 
    width: 100%;
    height: inherit;
    position: absolute;
    top: 0;
    left: 0;
    background-size: cover;
    background-position: 50% 50%;
    background-repeat: no-repeat;
    opacity: 0;
    z-index: 0;
    animation: imageAnimation 18s linear infinite; 
    }
      
    li:nth-child(1) { 
    background-image: url(https://previews.123rf.com/images/doomu/doomu1902/doomu190200316/117787333-red-green-yellow-and-blue-recycle-bins-with-recycle-symbol-on-a-white-background-3d-rendering.jpg); 
    }
    li:nth-child(2) { 
    background-image: url(https://static.turbosquid.com/Preview/2019/11/06__10_25_33/trashcansig0000.jpg8F7888A9-C14B-462D-9907-D8117E0DF02AZoom.jpg);
    animation-delay: 6s; 
    }
    li:nth-child(3) { 
    background-image: url(https://en.pimg.jp/047/452/504/1/47452504.jpg);
    animation-delay: 12s; 
    }
      
    @keyframes imageAnimation { 
        0% { 
        opacity: 0; 
        animation-timing-function: ease-in;
        }
        10% {
        opacity: 1;
        animation-timing-function: ease-out;
        }
        20% {
        opacity: 1
        }
        30% {
        opacity: 0
        }
    }
`;