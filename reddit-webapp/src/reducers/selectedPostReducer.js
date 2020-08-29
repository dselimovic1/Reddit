import {CREATE_COMMENT, DOWNVOTE, DOWNVOTE_COMMENT, FETCH_POST, UPVOTE, UPVOTE_COMMENT} from "../actions/types";

export default (state = {}, action) => {
    switch (action.type) {
        case FETCH_POST:
            return action.payload;
        case UPVOTE_COMMENT:
            const commentsAfterUpvote = state.comments.map(c => (c.id === action.payload.id ? action.payload : c));
            return { ...state, comments: commentsAfterUpvote };
        case DOWNVOTE_COMMENT:
            const comments = state.comments.map(c => (c.id === action.payload.id ? action.payload : c));
            return { ...state, comments };
        case UPVOTE:
            return action.payload;
        case DOWNVOTE:
            return action.payload;
        case CREATE_COMMENT:
            const addedComments = state.comments.concat(action.payload);
            return { ...state, comments: addedComments};
        default:
            return state;
    }
};