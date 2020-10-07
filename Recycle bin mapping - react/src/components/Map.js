import React from "react";
import RecycleBinInfo from "./RecycleBinInfo";
import { RecycleBinMarker, MapWrapper, FilterButton, Filter } from "./style";
import GoogleMapReact from "google-map-react";
import route from "../apis/route";

class Map extends React.Component {
    TYPES = {
        glass: 'glass',
        paper: 'paper',
        plastic: 'plastic',
        metal: 'metal'
    }
    COLORS = {
        'glass': "yellow",
        'metal': "greenyellow",
        'paper': "red",
        'plastic': "skyblue"
    }
    API_KEY = "AIzaSyAkhVpj9AMu-8Q6sAsuAXWuywA9srk4RG8";
    HEADERS = { 'Accept': 'application/json', 'Content-Type': 'application/json' };
    VISIBILITY = {
        yes: "yes",
        no: "no"
    }
    INTERVAL_TIME = {
        short: "5000",
        long: "50000"
    }

    state = {
        user: JSON.parse(window.sessionStorage.getItem('userData')),
        recycleBinData: null,
        recycleBinsMarkers: [],
        binsMarkers: [],
        binsPages: {
            'glass': 0,
            'paper': 0,
            'plastic': 0,
            'metal': 0,
        },
        pageSize: 100,
        isInfoVisible: this.VISIBILITY.no,
        filters: ['glass', 'paper', 'metal', 'plastic'],
        radious: 5,
        lat: 32.0128775,
        lng: 34.780945,
    }

    componentDidMount() {
        navigator.geolocation.getCurrentPosition((position) => {
            this.setState({lat: position.coords.latitude, lng : position.coords.longitude})
        });
        this.intervalId = setInterval(this.getBins, this.state.intervalTime);
    }
    
    componentWillUnmount(){
        clearInterval(this.intervalId);
    }

    RecycleBin = ({ text, bgColor, onClick }) => (
        <RecycleBinMarker onClick={onClick} bgColor={bgColor}>
            {text}
        </RecycleBinMarker>
    );

    handleFilterClick = filterName => {
        const isFiltered = this.state.filters.includes(filterName);
        const newFilters = this.state.filters;
        if (isFiltered) {
            newFilters.filter(filter => filter === filterName);
            
        } else {
            newFilters.push(filterName);
        }
        this.setState({filters: newFilters});
    };

    getBins = () => {
        const HEADERS = { 'Accept': 'application/json', 'Content-Type': 'application/json' };
        var newMarkers = this.state.binsMarkers;
        const newPages = {}
        for (const val of Object.values(this.TYPES)) {
            newPages[val] = this.state.binsPages[val] 
            const requestBody = {
                "type": "Search",
                "element": "1",
                "invokedBy": this.state.user ? this.state.user.email : '',
                "actionAttributes": {
                    "lat": this.state.lat,
                    "lng": this.state.lng,
                    "radious": this.state.radious,
                    "type": "recyclebin",
                    "name": val,
                    "pageNum": newPages[val],
                    "pageSize": 10
                }
            }
            route.post(`/actions/`, requestBody, { HEADERS }).then(res => {
                if (res.data.length > 0) {
                    console.log("data came");
                    res.data.map(item => {
                        newMarkers.push(item);
                    })
                    newPages[val] = newPages[val] + 1;
                }
            }).catch(err => alert(err.response.data.message))
        }
        if(newMarkers.length === this.state.binsMarkers.length){
            clearInterval(this.intervalId);
            this.intervalId = setInterval(this.getBins, this.INTERVAL_TIME.long);
        } else {
            clearInterval(this.intervalId);
            this.intervalId = setInterval(this.getBins, this.INTERVAL_TIME.short);
        }
        this.setState({ binsMarkers: newMarkers, binsPages: newPages})
       
    }

    handleMapClick = e => {
        e.clientY < 710 && this.setState({ isInfoVisible: this.VISIBILITY.no });
        
    };

    handleRecycleBinClick = (e, recycleBinMarker) => {
        const isVisible = this.state.isInfoVisible === this.VISIBILITY.yes ? this.VISIBILITY.no : this.VISIBILITY.yes;
        this.setState({ recycleBinData: recycleBinMarker, isInfoVisible: isVisible });
        e.stopPropagation();

    };

    handleFilterClick = filterName => {
        const isFiltered = this.state.filters.includes(filterName);
        const newFilters = this.state.filters;
        if (isFiltered) {
            this.setState({ filters: newFilters.filter(filt => filt !== filterName)});
        } else {
            newFilters.push(filterName);
            this.setState({ filters: newFilters });
        }
        
    };

    linkStyle = {
        textDecoration: "initial",
        color: "blueviolet"
    };

    getMarkersToRender = () => {
        const res = [];
        for (const key in this.state.binsMarkers) {
            res.concat(this.state.binsMarkers[key])
        }
        console.log(res)
        return res;
    }

    centerMoved = (map) => {
        this.setState({lat: map.getCenter().lat(), lng: map.getCenter().lng()});
    };

    render = () => {
        console.log("lat: " + this.state.lat + " lng: " + this.state.lng)
        console.log(this.state.binsMarkers)
        return (
            <MapWrapper>
                <GoogleMapReact
                    bootstrapURLKeys={{ key: this.API_KEY }}
                    defaultCenter={{ lat: 32.0128775, lng: 34.780945 }}
                    defaultZoom={18} 
                    onDragEnd={this.centerMoved}
                    ref={(ref) => {
                        this.mapRef = ref;
                      }}
                >
                    {this.state.binsMarkers.map((marker, index) => {
                        return (
                            !this.state.filters.includes(marker.name) ?
                            
                                <this.RecycleBin
                                    key={index}
                                    lat={marker.location.lat}
                                    lng={marker.location.lng}
                                    text={marker.name}
                                    bgColor={this.COLORS[marker.name]}
                                    onClick={e => {
                                        this.handleRecycleBinClick(e, marker);
                                    }}
                                />
                            
                         : null);
                    })}
                </GoogleMapReact>
                {<RecycleBinInfo data={this.state.recycleBinData} isVisible={this.state.isInfoVisible} />}
                <FilterButton>
                    <Filter backgroundColor={this.COLORS['glass']}
                        onClick={() => {
                            this.handleFilterClick("glass");
                        }}
                    >
                        {this.state.filters.find(filt => filt === "glass") ? 'show glass' : 'hide glass'}
                    </Filter>
                    <Filter backgroundColor={this.COLORS['metal']}
                        onClick={() => {
                            this.handleFilterClick("metal");
                        }}
                    >
                        {this.state.filters.find(filt => filt === "metal") ? 'show metal' : 'hide metal'}
                    </Filter>
                    <Filter backgroundColor={this.COLORS['paper']}
                        onClick={() => {
                            this.handleFilterClick("paper");
                        }}
                    >
                        {this.state.filters.find(filt => filt === "paper") ? 'show paper' : 'hide paper'}
                    </Filter>
                    <Filter backgroundColor={this.COLORS['plastic']}
                        onClick={() => {
                            this.handleFilterClick("plastic");
                        }}
                    >
                        {this.state.filters.find(filt => filt === "plastic") ? 'show plastic' : 'hide plastic'}
                    </Filter>
                </FilterButton>
            </MapWrapper>
        );
    };
}
export default Map;