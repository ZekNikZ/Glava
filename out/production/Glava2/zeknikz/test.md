# Shorthands
Shorthands are single- or double-character shortcuts that are replaced by longer strings. Shorthands are what give Glava much of its power.

## Type Shorthands
### Type Keywords
 - `β` → `boolean`
 - `χ` → `char`
 - `δ` → `double`
 - `φ` → `float`
 - `ι` → `int`
### Type Classes
 - `Π` → `Boolean`
 - `Σ` → `Character`
 - `Δ` → `Double`
 - `Φ` → `Float`
 - `Γ` → `Integer`
 - `σ` → `String`
### Type Classes w/ `new` Keyword
 - `ηΠ` → `new Boolean(` *
 - `ηΣ` → `new Character(` *
 - `ηΔ` → `new Double(` *
 - `ηΦ` → `new Float(` *
 - `ηΓ` → `new Integer(` *
 - `ησ` → `new String(` *
### Casting
 - `ζΠ` → `(boolean)`
 - `ζΣ` → `(char)`
 - `ζΔ` → `(double)`
 - `ζΦ` → `(float)`
 - `ζΓ` → `(int)`
 - `ζσ` → `(String)`
## Loop Shorthands
 - `ε` → `while(` _closing character adds `){`_ *
 - `Ξ` → `while(true){` (infinite `while` loop) *
 - `ω` → `for(` _closing character adds `){`_ *
 - `Ω` → `for(int i=0;i<` (pre-built `for` loop) _closing character adds `;i++){`_ *
## Command-line Arguments
### String Arguments
 - `⑴` → `args[0]`
 - `⑵` → `args[1]`
 - `⑶` → `args[2]`
 - `⑷` → `args[3]`
 - `⑸` → `args[4]`
 - `⑹` → `args[5]`
 - `⑺` → `args[6]`
 - `⑻` → `args[7]`
 - `⑼` → `args[8]`
 - `⑽` → `args[9]`
### Converted Arguments
The shorthands `①②③④⑤⑥⑦⑧⑨⑩` are replaced with their corresponding command-line arguments, after attempting to convert them to another native type. For example, if the first argument is `12.34`, `①` will be replaced with `12.34` in the code.
### Converted Arguments Arrays
All of these arrays contain only the command-line arguments of that time.
 - `Ⓑ` → `glava_args_boolean[` *
 - `Ⓒ` → `glava_args_char[` *
 - `Ⓓ` → `glava_args_double[` *
 - `Ⓕ` → `glava_args_float[` *
 - `Ⓘ` → `glava_args_int[` *
 - `Ⓢ` → `glava_args_String[` *
## Printing
 - `π` → `System.out.print(` *
 - `λ` → `System.out.println(` *
 - `κ` → `System.out.printf(` *
## Misc
 - `υ` → `.length()`
 - `ύ` → `.length`
 - `ϋ` → `.size()`
 - `η` → `null`

\* This shorthand opens a group.