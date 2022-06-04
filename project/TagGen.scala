import sbt._
import scala.sys.process._

object TagGen {

  def genTag(tag: TagType): String =
    tag match {
      case HasChildren(name, tag) => genTagHasChildren(name, tag)
      case NoChildren(name, tag)  => genTagNoChildren(name, tag)
      case OptionalChildren(name, tag) => genTagHasChildren(name, tag) + genTagNoChildren(name, tag)
    }

  def genTagHasChildren(tagName: String, tagRealName: Option[String]): String = {
    val tag = tagRealName.getOrElse(tagName)
    s"""  def $tagName[M](attributes: Attr[M]*)(children: Elem[M]*): Html[M] =
    |    Tag("$tag", attributes.toList, children.toList)
    |  @targetName("$tagName-list-repeated")
    |  def $tagName[M](attributes: List[Attr[M]])(children: Elem[M]*): Html[M] =
    |    Tag("$tag", attributes, children.toList)
    |  @targetName("$tagName-no_attrs-repeated")
    |  def $tagName[M](children: Elem[M]*): Html[M] =
    |    Tag("$tag", Nil, children.toList)
    |  @targetName("$tagName-repeated-list")
    |  def $tagName[M](attributes: Attr[M]*)(children: List[Elem[M]]): Html[M] =
    |    Tag("$tag", attributes.toList, children)
    |  @targetName("$tagName-list-list")
    |  def $tagName[M](attributes: List[Attr[M]])(children: List[Elem[M]]): Html[M] =
    |    Tag("$tag", attributes, children)
    |  @targetName("$tagName-no_attrs-list")
    |  def $tagName[M](children: List[Elem[M]]): Html[M] =
    |    Tag("$tag", Nil, children)
    |  @targetName("$tagName-repeated-list-plaintext")
    |  def $tagName[M](attributes: Attr[M]*)(plainText: String): Html[M] =
    |    Tag("$tag", attributes.toList, List(text(plainText)))
    |  @targetName("$tagName-list-list-plaintext")
    |  def $tagName[M](attributes: List[Attr[M]])(plainText: String): Html[M] =
    |    Tag("$tag", attributes, List(text(plainText)))
    |  @targetName("$tagName-no_attrs-list-plaintext")
    |  def $tagName[M](plainText: String): Html[M] =
    |    Tag("$tag", Nil, List(text(plainText)))
    |
    |""".stripMargin
  }

  def genTagNoChildren(tagName: String, tagRealName: Option[String]): String = {
    val tag = tagRealName.getOrElse(tagName)
    s"""  def $tagName[M](attributes: Attr[M]*): Html[M] =
    |    Tag("$tag", attributes.toList, Nil)
    |  @targetName("$tagName-list")
    |  def $tagName[M](attributes: List[Attr[M]]): Html[M] =
    |    Tag("$tag", attributes, Nil)
    |  @targetName("$tagName-no_attrs-list")
    |  def $tagName[M]: Html[M] =
    |    Tag("$tag", Nil, Nil)
    |
    |""".stripMargin
  }

  def template(moduleName: String, fullyQualifiedPath: String, contents: String): String =
    s"""package $fullyQualifiedPath
    |
    |import tyrian.Html.text
    |import scala.annotation.targetName
    |
    |// GENERATED by TagGen.scala - DO NOT EDIT
    |trait $moduleName {
    |
    |$contents
    |}
    """.stripMargin

  def gen(fullyQualifiedPath: String, sourceManagedDir: File): Seq[File] = {
    println("Generating Html Tags")

    Seq(htmlTagList, svgTagList).map { case TagList(tags, name) =>
      val contents: String =
        tags.map(genTag).mkString

      val file: File =
        sourceManagedDir / s"$name.scala"

      val newContents: String =
        template(name, fullyQualifiedPath, contents)

      IO.write(file, newContents)

      println("Written: " + file.getCanonicalPath)

      file
    }
  }

  def htmlTags: List[TagType] =
    List(
      HasChildren("a"),
      HasChildren("abbr"),
      HasChildren("address"),
      NoChildren("area"),
      HasChildren("article"),
      HasChildren("aside"),
      HasChildren("audio"),
      HasChildren("b"),
      NoChildren("base"),
      HasChildren("bdi"),
      HasChildren("bdo"),
      HasChildren("blockquote"),
      HasChildren("body"),
      NoChildren("br"),
      OptionalChildren("button"),
      HasChildren("canvas"),
      HasChildren("caption"),
      HasChildren("cite"),
      HasChildren("code"),
      NoChildren("col"),
      HasChildren("colgroup"),
      HasChildren("data"),
      HasChildren("datalist"),
      HasChildren("dd"),
      HasChildren("del"),
      HasChildren("details"),
      HasChildren("dfn"),
      HasChildren("dialog"),
      HasChildren("div"),
      HasChildren("dl"),
      HasChildren("dt"),
      HasChildren("em"),
      HasChildren("embed"),
      HasChildren("fieldset"),
      HasChildren("figcaption"),
      HasChildren("figure"),
      HasChildren("footer"),
      HasChildren("form"),
      HasChildren("h1"),
      HasChildren("h2"),
      HasChildren("h3"),
      HasChildren("h4"),
      HasChildren("h5"),
      HasChildren("h6"),
      HasChildren("head"),
      HasChildren("header"),
      NoChildren("hr"),
      HasChildren("html"),
      HasChildren("i"),
      HasChildren("iframe"),
      NoChildren("img"),
      NoChildren("input"),
      HasChildren("ins"),
      HasChildren("kbd"),
      HasChildren("label"),
      HasChildren("legend"),
      HasChildren("li"),
      NoChildren("link"),
      HasChildren("main"),
      HasChildren("map"),
      HasChildren("mark"),
      NoChildren("meta"),
      HasChildren("meter"),
      HasChildren("nav"),
      HasChildren("noscript"),
      HasChildren("`object`", "object"),
      HasChildren("objectTag", "object"),
      HasChildren("ol"),
      HasChildren("optgroup"),
      HasChildren("option"),
      HasChildren("output"),
      HasChildren("p"),
      NoChildren("param"),
      HasChildren("picture"),
      HasChildren("pre"),
      HasChildren("progress"),
      HasChildren("q"),
      HasChildren("rp"),
      HasChildren("rt"),
      HasChildren("ruby"),
      HasChildren("s"),
      HasChildren("samp"),
      HasChildren("script"),
      HasChildren("section"),
      HasChildren("select"),
      HasChildren("small"),
      NoChildren("source"),
      HasChildren("span"),
      HasChildren("strong"),
      HasChildren("style"),
      HasChildren("sub"),
      HasChildren("summary"),
      HasChildren("sup"),
      HasChildren("svg"),
      HasChildren("table"),
      HasChildren("tbody"),
      HasChildren("td"),
      HasChildren("template"),
      HasChildren("textarea"),
      HasChildren("tfoot"),
      HasChildren("th"),
      HasChildren("thead"),
      HasChildren("time"),
      HasChildren("title"),
      HasChildren("tr"),
      NoChildren("track"),
      HasChildren("u"),
      HasChildren("ul"),
      HasChildren("`var`", "var"),
      HasChildren("varTag", "var"),
      HasChildren("video"),
      HasChildren("wbr")
    )

    def svgTags: List[TagType] =
      List(
        NoChildren("animate"),
        NoChildren("animateColor"),
        NoChildren("animateMotion"),
        NoChildren("animateTransform"),
        NoChildren("circle"),
        HasChildren("clipPath"),
        HasChildren("defs"),
        HasChildren("desc"),
        NoChildren("ellipse"),
        NoChildren("feBlend"),
        NoChildren("feColorMatrix"),
        HasChildren("feComponentTransfer"),
        NoChildren("feComposite"),
        NoChildren("feConvolveMatrix"),
        HasChildren("feDiffuseLighting"),
        NoChildren("feDisplacementMap"),
        NoChildren("feDistantLight"),
        NoChildren("feFlood"),
        NoChildren("feFuncA"),
        NoChildren("feFuncB"),
        NoChildren("feFuncG"),
        NoChildren("feFuncR"),
        NoChildren("feGaussianBlur"),
        NoChildren("feImage"),
        HasChildren("feMerge"),
        NoChildren("feMergeNode"),
        NoChildren("feMorphology"),
        NoChildren("feOffset"),
        NoChildren("fePointLight"),
        HasChildren("feSpecularLighting"),
        NoChildren("feSpotLight"),
        HasChildren("feTile"),
        NoChildren("feTurbulence"),
        HasChildren("filter"),
        HasChildren("foreignObject"),
        HasChildren("g"),
        NoChildren("image"),
        NoChildren("line"),
        HasChildren("linearGradient"),
        HasChildren("marker"),
        HasChildren("mask"),
        HasChildren("metadata"),
        NoChildren("mpath"),
        NoChildren("path"),
        HasChildren("pattern"),
        NoChildren("polygon"),
        NoChildren("polyline"),
        HasChildren("radialGradient"),
        NoChildren("rect"),
        NoChildren("set"),
        NoChildren("stop"),
        HasChildren("switch"),
        HasChildren("symbol"),
        HasChildren("textTag", "text"),
        HasChildren("textPath"),
        HasChildren("tspan"),
        NoChildren("use"),
        NoChildren("view")
      )

    def htmlTagList = TagList(htmlTags, "HtmlTags")
    def svgTagList  = TagList(svgTags, "SVGTags")
}

sealed trait TagType
final case class HasChildren(name: String, tag: Option[String]) extends TagType
object HasChildren {
  def apply(name: String): HasChildren = HasChildren(name, None)
  def apply(name: String, tag: String): HasChildren = HasChildren(name, Some(tag))
}
final case class NoChildren(name: String, tag: Option[String])  extends TagType
object NoChildren {
  def apply(name: String): NoChildren = NoChildren(name, None)
  def apply(name: String, tag: String): NoChildren = NoChildren(name, Some(tag))
}
final case class OptionalChildren(name: String, tag: Option[String])  extends TagType
object OptionalChildren {
  def apply(name: String): OptionalChildren = OptionalChildren(name, None)
  def apply(name: String, tag: String): OptionalChildren = OptionalChildren(name, Some(tag))
}


final case class TagList(tags: List[TagType], namespace: String)
