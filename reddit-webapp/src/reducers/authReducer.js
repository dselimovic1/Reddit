import { SIGN_OUT, SIGN_IN } from "../actions/types";

const DEFAULT_STATE = {
    isSignedIn: null,
    jwt: null
};

export default (state = DEFAULT_STATE, action) => {
    switch (action.type) {
        case SIGN_IN:
            return { isSignedIn: true, jwt: action.payload };
        case SIGN_OUT:
            return { isSignedIn: false, jwt: null };
        default:
            return state;
    }
};
