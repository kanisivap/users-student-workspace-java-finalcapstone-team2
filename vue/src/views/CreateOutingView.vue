<template>
    <div id="main-div-outing">
        <h1>Create Outing</h1>
        <div id="fullForm">

            <label id="nameLabel" for="nameInput">Name Your Outing:&nbsp;</label>
            <input id="nameInput" name="nameInput" type="text" v-model="outing.name" v-bind:required="true" />

            <label id="zipLabel" for="zipcodeInput">Enter a Zipcode:&nbsp;</label>
            <input id="zipcodeInput" name="zipcodeInput" type="text" v-model="outing.zipCode" />

            <label id="deadLabel" for="deadlineInput">Date of final decision:&nbsp;</label>
            <input id="deadlineInput" name="deadlineInput" type="datetime-local" v-model="outing.dateDeadline"
                v-bind:required="true" />

            <label id="eventLabel" for="eventInput">Date of actual event:&nbsp;</label>
            <input id="eventInput" name="eventInput" type="datetime-local" v-model="outing.dateEvent"
                v-bind:required="true" />


        </div>

        <nav class="nav-div-search">
            <router-link v-bind:to="{ name: 'restaurantsSearch' }">Search Restaurant to Add to Your Outing
            </router-link>
        </nav>
        <div class="outing-list" v-for="item in outing.outingRestaurants" v-bind:key="item.id">
            {{ item.name }}

        </div>
        <div id="add-guests" class="nav-div-search">
            <button id="add-guest-btn" v-on:click="toggleIsFormShown">Add Guests</button>
        </div>
        <div class="outing-list" v-for="item in guests" v-bind:key="item.id">
            {{ item.name }} ({{ item.email }})

        </div>
        <form v-show="isFormShown" @submit.prevent id="guestForm">
            <div class="input-div">
                <label id="guestLabel" for="guestName">Guest's Name:</label>
                <input id="guestName" type="text" v-model="guest.name" />
            </div>
            <div class="input-div">
                <label id="emailLabel" for="guestEmail">Guest's Email:</label>
                <input id="guestEmail" type="text" v-model="guest.email" />
            </div>
            <div id="button-div">
                <button type="submit" v-on:click="addGuest">Save</button>
                <!-- click to submit, adds guest's name -->
            </div>
        </form>
        <nav>
            <router-link v-bind:to="{ name: 'home' }" v-on:click="clearOutingRestaurantsFromStoreAndAddOutingToSql">Finalize
                Your Outing
            </router-link>
        </nav>

        <!-- <div id="button-div">
            <button type="submit">Create Outing</button>
        </div> -->
    </div>
    <!-- <nav>
        <router-link v-bind:to="{ name: 'restaurantsSearch' }">Search Restaurant to Add to Your Outing
        </router-link>
    </nav> -->
</template>
<!-- Does this push user to restaurants list? -->

<script>
import RestaurantService from '../services/RestaurantService';
export default {
    data() {
        return {
            outing: {
                name: '',
                zipCode: '',
                dateDeadline: '',
                dateEvent: '',
                creator: this.$store.state.user.id,
                guests: [],
                outingRestaurants: [],

            },
            outingToPost: {
                name: '',
                zipCode: '',
                dateDeadline: '',
                dateEvent: '',
                creatorId: '',
                guests: [],
                outingRestaurants: []
            },
            localStorageOfStoreRestaurants: this.$store.state.storeOfRestaurantsInOuting,

            iteratedRestaurants: [],

            isFormShown: false,

            guest: {
                name: '',
                email: ''
            }
        }
    },


    methods: {
        // getNamesOfRestaurants() {
        //     let restaurantNames = this.outing.restaurants[0].map(item => item.name);
        //     return restaurantNames.join(', ');
        // }, 


        // iterateThroughOutingRestaurants() {
        //     this.outing.outingRestaurants = this.localStorageOfStoreRestaurants.flat();
        // },

        clearOutingRestaurantsFromStoreAndAddOutingToSql() {
            this.filterOuting();
            this.addOutingToSql(this.outingToPost);
            this.$store.commit('CLEAR_OUTING_RESTAURANTS');

        },

        iterateThroughOutingRestaurants() {
            let uniqueRestaurantsMap = new Map();

            this.localStorageOfStoreRestaurants.flat().forEach((restaurant) => {
                if (!uniqueRestaurantsMap.has(restaurant.id)) {
                    uniqueRestaurantsMap.set(restaurant.id, restaurant);
                }
            });

            this.outing.outingRestaurants = Array.from(uniqueRestaurantsMap.values());
        },

        toggleIsFormShown() {
            this.isFormShown = !this.isFormShown;
        },

        resetForm() {
            this.guest = {};
            this.toggleIsFormShown();
        },

        clearOutingGuestsFromStore() {
            this.$store.commit('CLEAR_GUESTS_RESTAURANTS')
        },

        iterateThroughGuests() {
            let uniqueGuestsMap = new Map();

            this.outing.guests.flat().forEach((guest) => {
                if (!uniqueGuestsMap.has(guest.email)) {
                    uniqueGuestsMap.set(guest.email, guest);
                }
            });

            this.guests = Array.from(uniqueGuestsMap.values());
        },

        linkNewGuestToStore() {
            this.$store.commit('SET_OUTING_GUESTS', this.guests)
        },

        addGuest() {
            this.outing.guests.push(this.guest);
            this.resetForm();
            this.iterateThroughGuests();

        },

        addOutingToSql(outing) {
            //does this need a .then()? 
            RestaurantService.addOuting(outing)
        },

        filterOuting() {
            this.outingToPost.name = this.outing.name;
            this.outingToPost.creatorId = this.outing.creator;
            this.outingToPost.zipCode = this.outing.zipCode;
            this.outingToPost.dateDeadline = this.outing.dateDeadline;
            this.outingToPost.dateEvent = this.outing.dateEvent;
            for (let i = 0; i < this.outing.guests.length; i++) {
                this.outingToPost.guests.push({
                    emailAddress: this.outing.guests[i].email,
                    name: this.outing.guests[i].name
                });
            }
            for (let i = 0; i < this.outing.outingRestaurants.length; i++) {
                this.outingToPost.outingRestaurants.push({
                    longRestaurantId: this.outing.outingRestaurants[i].id,
                    restaurantName: this.outing.outingRestaurants[i].name
                });
            }
            return this.outingToPost;
        }

    },


    mounted() {
        this.iterateThroughOutingRestaurants();
        this.iterateThroughGuests();
    },

    computed: {
        currentToken() {
            return this.$store.state.token;
        }
    }



};
</script>

<style>
#main-div-outing {

    padding: 10px;
    background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('/create.jpg');
    background-size: cover;
    background-position: center;
    height: 100vh;


}

h1,
p {
    text-align: center;
}

#button-div {
    grid-area: button;
    display: flex;
    text-align: center;
    padding-top: 15px;
    justify-content: center;
    align-items: center;
}

.input-div {
    padding: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 5px;
}

button {
    text-align: center;
    width: 150px;
    height: 50px;
}

.nav-div-search {

    padding: 1%;
    margin-top: 20px;
    margin-right: 20%;
    margin-left: 20%;
    text-align: center;
}

.outing-list {
    background-color: rgba(52, 45, 45, 0.8);
    margin-left: 20%;
    margin-right: 20%;
    padding: 1%;
    border-radius: 25px;
    color: white;
    margin-bottom: 15px;
   

}

#fullForm{
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-areas: 
        "nameLabel nameIn"
        "zipLabel zipIn"
        "deadLabel deadIn"
        "eventLabel eventIn";
        
    background-color: rgb(214,20,6, 0.7) ;
    gap: 10px;
    border-radius: 25px;
    padding-left: 25px;
    padding-right: 25px;
    padding-bottom: 20px;
    padding-top: 20px;
    color: white;
    margin-left: 25px;
    margin-right: 25px;
    
}
#guestForm{
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-areas: 
        "guestLabel nameInput"
        "guestEmail emailInput"
        "button button";
    background-color: rgb(214,20,6, 0.7) ;
    gap: 10px;
    border-radius: 25px;
    padding-left: 25px;
    padding-right: 25px;
    padding-bottom: 20px;
    padding-top: 20px;
    color: white;
    margin-left: 25px;
    margin-right: 25px;
    
}
#guestLabel{
    grid-area: guestLabel;
}
#guestName{
    grid-area: nameInput;
}
#emailLabel{
    grid-area: guestEmail;
}
#guestEmail{
    grid-area: emailInput;
}

#nameLabel{
    grid-area: nameLabel;
}
#zipLabel{
    grid-area: zipLabel;
}
#deadLabel{
    grid-area: deadLabel;
}
#eventLabel{
    grid-area: eventLabel;
}
#nameInput{
    grid-area: nameIn;
}
#zipcodeInput{
    grid-area: zipIn;
}
#deadlineInput{
    grid-area: deadIn;
}
#eventInput{
    grid-area: eventIn;
}
input{
    display: flex;
    height: 30px;
    width: 300px;
    align-self: center;
    margin-left: 200px;
}
button {
    padding: 8p 14px;
    cursor: pointer;
    border: 2px solid #0f172a;
    box-shadow: 2px 2px 0 0 #ffffff, 4px 4px 0 0 #0f172a;
    transition-duration: 200ms;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    border-radius: 10px;
    font-size: large
}

button:hover {
    box-shadow: 3px 3px #ffffff, 6px 6px 0 0 #0f172a;
    transform: translate(-2px, -2px);
}


/* <div id="fullForm">

<label id="nameLabel" for="nameInput">Name Your Outing:&nbsp;</label>
<input id="nameInput" name="nameInput" type="text" v-model="outing.name" v-bind:required="true" />

<label id="zipLabel" for="zipcodeInput">Enter a Zipcode:&nbsp;</label>
<input id="zipcodeInput" name="zipcodeInput" type="text" v-model="outing.zipCode" />

<label id="deadLabel" for="deadlineInput">Date of final decision:&nbsp;</label>
<input id="deadlineInput" name="deadlineInput" type="datetime-local" v-model="outing.dateDeadline"
    v-bind:required="true" />

<label id="eventLabel" for="eventInput">Date of actual event:&nbsp;</label>
<input id="eventInput" name="eventInput" type="datetime-local" v-model="outing.dateEvent"
    v-bind:required="true" />


</div> */

</style>