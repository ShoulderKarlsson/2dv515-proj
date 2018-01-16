import React from 'react'
import ReactDOM from 'react-dom'
import {App} from './App'
import {injectGlobal} from 'styled-components'
import {BrowserRouter as Router, Route} from 'react-router-dom'
injectGlobal`
  html, body {
    margin: 0;
    padding: 0;
  }
`
ReactDOM.render(
  <Router>
    <Route path='/' component={App} />
  </Router>,
  document.getElementById('root'),
)
