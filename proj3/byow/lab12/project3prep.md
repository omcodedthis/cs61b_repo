# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
My initial implementation was similar to tactical programming, just creating that something that had worked. However,
by adopting the steps of strategic programming, I took into consideration the long-term structure of the system. I did 
this by creating a nested class that accounted for the position & using a recursive function to draw the hexagon.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:
The hexagon is the rooms & hallways and the tesselation is creating a world filled with these rooms & hallways.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:
I think I would focus on writing a method that generates a room & a hallway itself respectively.
-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:
A hallway is a straight line, whereas a room is more of a rectangle in terms of general shape. Both are similar in the 
way that both have an opening where the player can enter through.
