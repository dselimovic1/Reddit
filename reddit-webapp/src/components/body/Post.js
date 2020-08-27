import React from "react";
import { Link } from 'react-router-dom';
import { connect } from "react-redux";
import { upvote, downvote } from "../../actions";
import Rating from "../common/Rating";

const Post = ({ post, upvote, downvote }) => {
    return (
            <div className="ui icon message" style={{backgroundColor: 'white'}}>
                <Rating post={post} upvote={upvote} downvote={downvote}/>
                <div className="content" style={{paddingLeft: '10px', paddingRight: '10px'}}>
                    <span>
                        <p>
                            <Link to={`/topic/${post.topic.id}`} style={{marginRight: '5px'}}>{`r/${post.topic.name}`}</Link>
                            Posted by <Link to={`/users/${post.user.id}`}>{`u/${post.user.username}`}</Link>
                        </p>
                    </span>
                    <Link to={`/posts/${post.id}`} className="header" style={{marginBottom: '10px', marginTop: '10px'}}>{post.title}</Link>
                    <div className="meta">
                        <Link to={`/posts/${post.id}`} className="ui basic button">
                            <i className="comment icon"/>
                            {`${post.comments.length} comments`}
                        </Link>
                    </div>
                </div>
                <button className="ui right floated primary button">Join</button>
            </div>
    );
};

export default connect(null, { upvote, downvote })(Post);