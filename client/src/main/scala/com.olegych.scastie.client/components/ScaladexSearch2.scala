package com.olegych.scastie.client.components

import com.olegych.scastie.api._

// import play.api.libs.json.Json

import japgolly.scalajs.react._, vdom.all._, extra._
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope

// import org.scalajs.dom
// import dom.ext.KeyCode
// import dom.raw.{HTMLInputElement, HTMLElement}
// import dom.ext.Ajax

// import scalajs.concurrent.JSExecutionContext.Implicits.queue

final case class ScaladexSearch2(
    removeScalaDependency: ScalaDependency ~=> Callback,
    updateDependencyVersion: (ScalaDependency, String) ~=> Callback,
    addScalaDependency: (ScalaDependency, Project) ~=> Callback,
    librariesFrom: Map[ScalaDependency, Project],
    scalaTarget: ScalaTarget
) {
  @inline def render: VdomElement = ScaladexSearch2.component(this)
}

object ScaladexSearch2 {

  private[ScaladexSearch2] case class Selected(
      project: Project,
      release: ScalaDependency,
      options: ReleaseOptions
  )


  private[ScaladexSearch2] class ScaladexSearchBackend2(
      scope: BackendScope[ScaladexSearch2, SearchState2]
  ) {

  }

  private[ScaladexSearch2] object SearchState2 {
    def default: SearchState2 = SearchState2(
      query = "",
      selectedIndex = 0,
      projects = List.empty,
      selecteds = List.empty
    )

    def fromProps(props: ScaladexSearch2): SearchState2 = ???
  }

  private[ScaladexSearch2] case class SearchState2(
      query: String,
      selectedIndex: Int,
      projects: List[Project],
      selecteds: List[Selected]
  )

  private def render(
      scope: RenderScope[ScaladexSearch2, SearchState2, ScaladexSearchBackend2],
      props: ScaladexSearch2,
      searchState: SearchState2
  ): VdomElement = {
    ???
  }

  private val component =
    ScalaComponent
      .builder[ScaladexSearch2]("Scaladex Search2")
      .initialStateFromProps(SearchState2.fromProps)
      .backend(new ScaladexSearchBackend2(_))
      .renderPS(render)
      // .componentDidMount(_ => Callback(searchInputRef.focus))
      // .configure(Reusability.shouldComponentUpdate)
      .build
}