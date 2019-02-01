(defun debug-ignore (c h) (declare (ignore h)) (format t "*** LISP ERROR: ~A~%" c) (exit))
(setf *debugger-hook* #'debug-ignore)
