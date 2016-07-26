import understand
db = understand.open('../testdb/test_sonic.udb')

for func in db.ents('method'):
	if func.longname().endswith('run'):
		classtype = func.ref('DefineIn')

		'''
		Thread.run() - Not OK (Higher Priority)
		'''
		if classtype is not None:
			classtype = classtype.ent()
			parents = [ref.ent().name() for ref in classtype.refs("Couple")]

			if 'Thread' in parents:
				for calling_func in func.refs('call'):
					_file_ = calling_func.file()
					lno = calling_func.line()
					print (_file_, ', line ', lno, ': Did you intend to use Thread.start() to create a new execution thread?')

			'''
			Runnable.run() - Not OK (Lower Priority)
			Executor - OK
			'''
			elif ('Runnable' in parents) and not ('Executor' in parents):
				for calling_func in func.refs('call'):
					_file_ = calling_func.file()
					lno = calling_func.line()
					print (_file_, ', line ', lno, ': Did you intend to put run() method in new thread?')
