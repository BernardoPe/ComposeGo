package go.storage

import kotlin.io.path.*

class TextFileStorage<Key, Data>(val baseDirectory: String, private val serializer: Serializer<Data>): Storage<Key, Data> {
    init{
        with(Path(baseDirectory)){
            if(!exists()) createDirectory()
            else check(isDirectory()){"$name is not a directory"}
        }
    }

    private fun path(key: Key) = Path("$baseDirectory/$key.txt")

    override fun create(key: Key, data: Data) {
        path(key).let {
            check(!it.exists()) { "File $key already exists" }
            it.writeText(serializer.serialize(data))
        }
    }

    override fun read(key: Key): Data? =
        path(key).let {
            check(it.exists()) { "File $key does not exist" }
            serializer.deserialize(it.readText())
        }


    override fun update(key: Key, data: Data)=
        path(key).let {
            check(it.exists()) { "File $key does not exist" }
            it.writeText(serializer.serialize(data))
        }

    override fun delete(key: Key)  =
        check(path(key).deleteIfExists()) { "File $key does not exist" }

}