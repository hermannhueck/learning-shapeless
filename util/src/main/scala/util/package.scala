package object util {

  def prtTitle(
      text: String,
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): Unit =
    println(title(text, width, leading, trailing, fill))

  def title(
      text: String,
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): String = {
    s"${line(width, leading, "", fill)}${subTitle(text.toUpperCase, width, "\n", s"$trailing\n", fill)}"
  }

  def prtSubTitle(
      text: String,
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): Unit =
    println(subTitle(text, width, leading, trailing, fill))

  def subTitle(
      text: String,
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): String = {
    val frontPad = fill * 10
    val startLength = (10 + text.length() + 2)
    val endLength = if (startLength > width) 0 else width - startLength
    val endPad = fill * endLength
    s"$leading$frontPad $text $endPad$trailing"
  }

  def prtLine(
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): Unit =
    println(line(width, leading, trailing, fill))

  def line(
      width: Int = 80,
      leading: String = "",
      trailing: String = "",
      fill: String = "\u2500"
  ): String = {
    val line = fill * width
    s"$leading$line$trailing"
  }

  def objectNameSimple(scalaObject: java.lang.Object) = {
    val cn = scalaObject.getClass().getSimpleName()
    cn.substring(0, cn.length() - 1)
  }

  def objectName(scalaObject: java.lang.Object) = {
    val cn = scalaObject.getClass().getName
    cn.substring(0, cn.length() - 1)
  }

  def prtSubTitleObjectName(scalaObject: java.lang.Object) =
    prtSubTitle(objectName(scalaObject))

  def green(): Unit = print(Console.GREEN)
  def red(): Unit = print(Console.RED)
  def blue(): Unit = print(Console.BLUE)
  def yellow(): Unit = print(Console.YELLOW)
  def cyan(): Unit = print(Console.CYAN)
  def magenta(): Unit = print(Console.MAGENTA)
  def reset(): Unit = print(Console.RESET)
}
