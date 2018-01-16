import * as React from 'react'
import {Route} from 'react-router-dom'
import {Users} from './pages/users'
import {UserRecommendation} from './pages/user-recommendation'

export const App = () => {
  return (
    <div>
      <Route exact path="/" component={Users} />
      <Route exact path="/user/:username" component={UserRecommendation} />
    </div>
  )
}
