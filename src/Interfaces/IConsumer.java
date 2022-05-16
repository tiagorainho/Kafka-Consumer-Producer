package Interfaces;


public interface IConsumer<E> {

    public void consume(int id, E element);
}