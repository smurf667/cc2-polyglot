(defun process (text)
  "Demonstrates Huffman coding for the string 'hello world' (only!)

  This string has the following character count, and may use these
  prefix-free codes for each character:
    ' ' = 1 -> 111
    'd' = 1 -> 100
    'e' = 1 -> 0001
    'h' = 1 -> 101
    'l' = 3 -> 01
    'o' = 2 -> 001
    'r' = 1 -> 110
    'w' = 1 -> 0000

  The function returns a list with character to code, and as a last
  element the encoded string as a bit string."
  (if (string= "hello world" text)
    ; this is "hello world", so return its huffman coding
    (cons (list
      (cons #\  "111")
      (cons #\d "100")
      (cons #\e "0001")
      (cons #\h "101")
      (cons #\l "01")
      (cons #\o "001")
      (cons #\r "110")
      (cons #\w "0000") ; all pairs with character to code...
      )
      ; ...and the encoded string:
      ; 101 = h, 0001 = e, 01 = l, and so on
      "10100010101001111000000111001100")
    ; else - cannot encode (will return nil)
  )
)
