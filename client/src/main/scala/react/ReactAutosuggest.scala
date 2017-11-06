package react

import scala.scalajs.js
import js.annotation._

@JSImport("react-autosuggest", JSImport.Namespace)
@js.native
object ReactAutosuggestRaw extends js.Object


| suggestions                  | Array    | ✓                               | These are the suggestions that will be displayed. Items can take an arbitrary shape. |
| onSuggestionsFetchRequested  | Function | ✓                               | Will be called every time you need to recalculate `suggestions`. |
| onSuggestionsClearRequested  | Function | ✓                               | Will be called every time you need to set `suggestions` to `[]`. |
| getSuggestionValue           | Function | ✓                               | Implement it to teach Autosuggest what should be the input value when suggestion is clicked. |
| renderSuggestion             | Function | ✓                               | Use your imagination to define how suggestions are rendered. |
| inputProps                   | Object   | ✓                               | Pass through arbitrary props to the input. It must contain at least `value` and `onChange`. |
| onSuggestionSelected         | Function |                                 | Will be called every time suggestion is selected via mouse or keyboard. |
| onSuggestionHighlighted      | Function |                                 | Will be called every time the highlighted suggestion changes. |
| shouldRenderSuggestions      | Function |                                 | When the input is focused, Autosuggest will consult this function when to render suggestions. Use it, for example, if you want to display suggestions when input value is at least 2 characters long. |
| alwaysRenderSuggestions      | Boolean  |                                 | Set it to `true` if you'd like to render suggestions even when the input is not focused. |
| highlightFirstSuggestion     | Boolean  |                                 | Set it to `true` if you'd like Autosuggest to automatically highlight the first suggestion. |
| focusInputOnSuggestionClick  | Boolean  |                                 | Set it to `false` if you don't want Autosuggest to keep the input focused when suggestions are clicked/tapped. |
| multiSection                 | Boolean  |                                 | Set it to `true` if you'd like to display suggestions in multiple sections (with optional titles). |
| renderSectionTitle           | Function | ✓<br>when `multiSection={true}` | Use your imagination to define how section titles are rendered. |
| getSectionSuggestions        | Function | ✓<br>when `multiSection={true}` | Implement it to teach Autosuggest where to find the suggestions for every section. |
| renderInputComponent         | Function |                                 | Use it only if you need to customize the rendering of the input. |
| renderSuggestionsContainer   | Function |                                 | Use it if you want to customize things inside the suggestions container beyond rendering the suggestions themselves. |
| theme                        | Object   |                                 | Use your imagination to style the Autosuggest. |
| id                           | String   |                                 | Use it only if you have multiple Autosuggest components on a page. |



final case class ReactAutosuggest[T](
  suggestions: js.Array[T],
  onSuggestionsFetchRequested: String => Callback,
  onSuggestionsClearRequested: () => Callback,
  getSuggestionValue
  renderSuggestion
  inputProps
  onSuggestionSelected
  onSuggestionHighlighted
  shouldRenderSuggestions
  alwaysRenderSuggestions
  highlightFirstSuggestion
  focusInputOnSuggestionClick
  multiSection
  renderSectionTitle
  getSectionSuggestions
  renderInputComponent
  renderSuggestionsContainer
  theme
  id
){
  @inline def render: VdomElement = ReactAutosuggest.component(this)
}

// https://github.com/moroshko/react-autosuggest
object ReactAutosuggest {
  val component = JsComponent[ReactAutosuggest, Children.Varargs, Null](
    ReactAutosuggestRaw
  )
}