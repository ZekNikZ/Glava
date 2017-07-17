# Automatic Code-Completion
_Glava_ has the great feature of being able to auto-complete many parts of your code. This wiki page will outline some of these features.

## Automatic Group Closing
If, throughout your code, you open any groups (`(`, `{`, `[`, etc.) but never close them, _Glava_ will close them automatically when compiling. Note that this does **not** work the other way around.

Example: `λ"Hello, World!` will be closed by `λ"Hello, World!"`, and then by `System.out.println("Hello, World!)`.

## Automatic `main` Method and `class` Wrapping
_Glava_ also has the ability to wrap your entire code in a `main` method, assuming it does not have one. And then, if it does not have a surrounding `class`, it will wrap the code in `public class Main { ... }`.

Additionally, if _Glava_ wraps the code in a `class`, several other features are added as well. All of the following are added into/around the `Main` `class` as well:

### Default Imports
 - `java.util.*`
 - `static java.lang.Math.*`
 - `java.io.*`
 - `java.awt.*`
 - `javax.swing.*`
 - `java.text.*`
 - `java.util.regex.*`

### Default Class (Static) Variables
 - `int m = 0`
 - `int n = 1`
 - `double d = 0d`
 - `float f = 0f`
 - `String s = ""`
 - `String t = ""`
 - `String u = "abcdefghijklmnopqrstuvwxyz"`
 - `String U = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"`
 - `char c = 'A'`
 - `boolean T = true`
 - `boolean F = false`

### Command-line Converted Argument Arrays
Note: only the applicable arrays are created when the program is run. Also note that all of these arrays also have [shorthands](https://github.com/GamrCorps/Glava/wiki/Shorthands#converted-arguments-arrays).
 - `int[] glava_args_int`
 - `double[] glava_args_double`
 - `float[] glava_args_int`
 - `char[] glava_args_char`
 - `boolean[] glava_args_boolean`